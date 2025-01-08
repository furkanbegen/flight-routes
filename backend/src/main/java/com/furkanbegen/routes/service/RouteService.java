package com.furkanbegen.routes.service;

import com.furkanbegen.routes.dto.RouteDTO;
import com.furkanbegen.routes.exception.ResourceNotFoundException;
import com.furkanbegen.routes.mapper.RouteMapper;
import com.furkanbegen.routes.model.Location;
import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.repository.LocationRepository;
import com.furkanbegen.routes.validator.RouteValidator;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

  private final CacheableTransportationService cacheableTransportationService;
  private final LocationRepository locationRepository;
  private final RouteMapper routeMapper;
  private final RouteValidator routeValidator;

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

    // Use ID-based map for reliable lookups
    Map<Long, List<Transportation>> graph =
        cacheableTransportationService.findAll().stream()
            .collect(Collectors.groupingBy(t -> t.getFromLocation().getId()));

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
      Map<Long, List<Transportation>> graph, Location fromLocation, Location toLocation) {
    // Pre-size collections based on known constraints
    List<List<Transportation>> validRoutes = new ArrayList<>();
    Deque<Transportation> currentPath = new ArrayDeque<>(4);
    Set<Long> visited = HashSet.newHashSet(graph.size());

    findRoutesRecursive(graph, fromLocation, toLocation, currentPath, visited, validRoutes);
    validRoutes.forEach(Collections::reverse);
    return validRoutes;
  }

  private void findRoutesRecursive(
      Map<Long, List<Transportation>> graph,
      Location current,
      Location destination,
      Deque<Transportation> currentPath,
      Set<Long> visited,
      List<List<Transportation>> validRoutes) {

    if (currentPath.size() > 3) {
      return;
    }

    if (current.getId().equals(destination.getId()) && routeValidator.isValidPath(currentPath)) {
      log.info(
          "Found valid path: "
              + currentPath.stream()
                  .map(Transportation::getName)
                  .collect(Collectors.joining(" -> ")));
      validRoutes.add(new ArrayList<>(currentPath));
      return;
    }

    visited.add(current.getId());

    List<Transportation> possibleTransportations =
        graph.getOrDefault(current.getId(), Collections.emptyList());

    for (Transportation possibleTransportation : possibleTransportations) {
      Long nextLocationId = possibleTransportation.getToLocation().getId();

      if (visited.contains(nextLocationId)
          || !routeValidator.isValidAddition(currentPath, possibleTransportation)) {
        continue;
      }

      currentPath.addFirst(possibleTransportation);
      findRoutesRecursive(
          graph,
          possibleTransportation.getToLocation(),
          destination,
          currentPath,
          visited,
          validRoutes);
      currentPath.removeFirst();
    }

    visited.remove(current.getId());
  }
}
