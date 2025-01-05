package com.furkanbegen.routes.service;

import com.furkanbegen.routes.dto.RouteDTO;
import com.furkanbegen.routes.exception.ResourceNotFoundException;
import com.furkanbegen.routes.mapper.RouteMapper;
import com.furkanbegen.routes.model.Location;
import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.model.TransportationType;
import com.furkanbegen.routes.repository.LocationRepository;
import com.furkanbegen.routes.repository.TransportationRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouteService {

  private final TransportationRepository transportationRepository;
  private final LocationRepository locationRepository;
  private final RouteMapper routeMapper;

  public RouteService(
      TransportationRepository transportationRepository,
      LocationRepository locationRepository,
      RouteMapper routeMapper) {
    this.transportationRepository = transportationRepository;
    this.locationRepository = locationRepository;
    this.routeMapper = routeMapper;
  }

  public Page<RouteDTO> findRoutes(Long fromLocationId, Long toLocationId, Pageable pageable) {
    Location fromLocation =
        locationRepository
            .findById(fromLocationId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        String.format("Location not found with id: %d", fromLocationId)));

    Location toLocation =
        locationRepository
            .findById(toLocationId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        String.format("Location not found with id: %d", toLocationId)));

    Map<Location, List<Transportation>> graph =
        transportationRepository.findAll().stream()
            .collect(Collectors.groupingBy(Transportation::getFromLocation));

    List<List<Transportation>> validRoutes = findValidRoutes(graph, fromLocation, toLocation);
    log.info(
        "Found {} valid routes from {} to {}",
        validRoutes.size(),
        fromLocation.getName(),
        toLocation.getName());

    List<RouteDTO> routeDTOs = validRoutes.stream().map(routeMapper::convertToRouteDTO).toList();

    // Pagination
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), routeDTOs.size());
    List<RouteDTO> pageContent =
        start >= routeDTOs.size() ? Collections.emptyList() : routeDTOs.subList(start, end);

    return new PageImpl<>(pageContent, pageable, routeDTOs.size());
  }

  private List<List<Transportation>> findValidRoutes(
      Map<Location, List<Transportation>> graph, Location fromLocation, Location toLocation) {
    List<List<Transportation>> validRoutes = new ArrayList<>();
    Stack<Transportation> currentPath = new Stack<>();
    Set<Location> visited = new HashSet<>();

    findRoutesRecursive(graph, fromLocation, toLocation, currentPath, visited, validRoutes);

    return validRoutes;
  }

  private void findRoutesRecursive(
      Map<Location, List<Transportation>> graph,
      Location current,
      Location destination,
      Stack<Transportation> currentPath,
      Set<Location> visited,
      List<List<Transportation>> validRoutes) {

    // Path is not valid if there is more than 3 transportation
    if (currentPath.size() > 3) {
      return;
    }

    if (current.equals(destination) && isValidPath(currentPath)) {
      log.info(
          "Found valid path: "
              + currentPath.stream()
                  .map(Transportation::getName)
                  .collect(Collectors.joining(" -> ")));
      validRoutes.add(new ArrayList<>(currentPath));
      return;
    }

    visited.add(current);

    List<Transportation> possibleTransportations =
        graph.getOrDefault(current, Collections.emptyList());

    for (Transportation possibleTransportation : possibleTransportations) {
      Location nextLocation = possibleTransportation.getToLocation();

      if (visited.contains(nextLocation) || !isValidAddition(currentPath, possibleTransportation)) {
        continue;
      }

      currentPath.push(possibleTransportation);
      findRoutesRecursive(graph, nextLocation, destination, currentPath, visited, validRoutes);
      currentPath.pop();
    }

    visited.remove(current);
  }

  private boolean isValidPath(Stack<Transportation> path) {
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

  private boolean isValidAddition(
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
