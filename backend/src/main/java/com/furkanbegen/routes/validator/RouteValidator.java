package com.furkanbegen.routes.validator;

import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.model.TransportationType;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public boolean isValidPath(Deque<Transportation> path) {
    if (path.isEmpty()) {
      return false;
    }

    long flightCount =
        path.stream()
            .filter(transportation -> TransportationType.FLIGHT.equals(transportation.getType()))
            .count();

    // Must have exactly one flight
    if (flightCount != 1) {
      return false;
    }

    List<TransportationType> types = path.stream().map(Transportation::getType).toList();

    // If there's only one transportation, it must be a flight
    if (types.size() == 1) {
      return TransportationType.FLIGHT.equals(types.get(0));
    }

    int flightIndex = types.indexOf(TransportationType.FLIGHT);

    // All transportations before flight must be OTHER
    boolean validBefore =
        types.subList(0, flightIndex).stream().allMatch(TransportationType.OTHER::equals);

    // All transportations after flight must be OTHER
    boolean validAfter =
        types.subList(flightIndex + 1, types.size()).stream()
            .allMatch(TransportationType.OTHER::equals);

    return validBefore && validAfter;
  }

  public boolean isValidAddition(
      Deque<Transportation> currentPath, Transportation candidateTransportation) {

    if (currentPath.isEmpty()) {
      return true;
    }

    List<Transportation> potentialPath = new ArrayList<>(currentPath);
    potentialPath.add(candidateTransportation);

    if (exceedsFlightLimit(potentialPath)) {
      return false;
    }

    return isValidTransportationSequence(currentPath, candidateTransportation);
  }

  private boolean exceedsFlightLimit(List<Transportation> path) {
    long flightCount =
        path.stream()
            .filter(transportation -> TransportationType.FLIGHT.equals(transportation.getType()))
            .count();
    return flightCount > 1;
  }

  private boolean isValidTransportationSequence(
      Deque<Transportation> currentPath, Transportation candidateTransportation) {
    if (!TransportationType.OTHER.equals(candidateTransportation.getType())) {
      return true;
    }

    boolean containsFlight =
        currentPath.stream()
            .anyMatch(transportation -> TransportationType.FLIGHT.equals(transportation.getType()));

    if (!containsFlight) {
      return true;
    }

    return !hasOtherTransportationAfterFlight(currentPath);
  }

  private boolean hasOtherTransportationAfterFlight(Deque<Transportation> path) {
    boolean flightEncountered = false;
    for (Transportation transportation : path) {
      if (flightEncountered && TransportationType.OTHER.equals(transportation.getType())) {
        return true;
      }
      if (TransportationType.FLIGHT.equals(transportation.getType())) {
        flightEncountered = true;
      }
    }
    return false;
  }
}
