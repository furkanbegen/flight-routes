package com.furkanbegen.routes.service;

import com.furkanbegen.routes.dto.LocationDTO;
import com.furkanbegen.routes.exception.*;
import com.furkanbegen.routes.mapper.LocationMapper;
import com.furkanbegen.routes.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

  private final LocationRepository locationRepository;
  private final LocationMapper locationMapper;

  public Page<LocationDTO> getAllLocations(Pageable pageable) {
    return locationRepository.findAll(pageable).map(locationMapper::toDTO);
  }

  public LocationDTO getLocationById(Long id) {
    var location =
        locationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

    return locationMapper.toDTO(location);
  }

  public LocationDTO createLocation(LocationDTO dto) {

    var location = locationMapper.toEntity(dto);

    return locationMapper.toDTO(locationRepository.save(location));
  }

  public LocationDTO updateLocation(Long id, LocationDTO dto) {

    var location =
        locationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

    var locationForUpdate = locationMapper.toEntity(dto, location);

    return locationMapper.toDTO(locationRepository.save(locationForUpdate));
  }

  public void deleteLocation(Long id) {
    var location =
        locationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

    locationRepository.delete(location);
  }

  public Page<LocationDTO> searchLocations(String query, Pageable pageable) {
    return locationRepository.searchByNameContainingIgnoreCase(query, pageable)
        .map(locationMapper::toDTO);
  }
}
