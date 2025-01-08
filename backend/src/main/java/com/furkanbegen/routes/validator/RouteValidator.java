package com.furkanbegen.routes.validator;

import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.model.TransportationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
public class RouteValidator {

  public boolean isValidPath(Stack<Transportation> path) {
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
      Stack<Transportation> currentPath, Transportation newTransportation) {

    if (currentPath.isEmpty()) {
      return true;
    }

    List<Transportation> pathWithNew = new ArrayList<>(currentPath);
    pathWithNew.add(newTransportation);

    long flightCount =
        pathWithNew.stream()
            .filter(transportation -> TransportationType.FLIGHT.equals(transportation.getType()))
            .count();

    // There can be at most one flight
    if (flightCount > 1) {
      return false;
    }

    // If we're trying to add OTHER after FLIGHT when we already have one after flight
    if (TransportationType.OTHER.equals(newTransportation.getType())) {
      boolean hasFlightInPath =
          currentPath.stream()
              .anyMatch(
                  transportation -> TransportationType.FLIGHT.equals(transportation.getType()));
      if (hasFlightInPath) {
        long afterFlightCount = 0;
        boolean flightFound = false;
        for (Transportation transportation : currentPath) {
          if (flightFound && TransportationType.FLIGHT.equals(transportation.getType())) {
            afterFlightCount++;
          }

          if (TransportationType.FLIGHT.equals(transportation.getType())) {
            flightFound = true;
          }
        }
        return afterFlightCount == 0;
      }
    }

    return true;
  }
}
