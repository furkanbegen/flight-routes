package com.furkanbegen.routes.mapper;

import com.furkanbegen.routes.dto.TransportationDTO;
import com.furkanbegen.routes.dto.TransportationRequestDTO;
import com.furkanbegen.routes.exception.ResourceNotFoundException;
import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransportationMapper {

  private final LocationRepository locationRepository;
  private final LocationMapper locationMapper;

  public Transportation toEntity(TransportationRequestDTO requestDTO) {
    if (requestDTO == null) {
      return null;
    }

    Transportation transportation = new Transportation();

    transportation.setFromLocation(
        locationRepository
            .findById(requestDTO.getFromLocationId())
            .orElseThrow(() -> new ResourceNotFoundException("Location not found")));
    transportation.setToLocation(
        locationRepository
            .findById(requestDTO.getToLocationId())
            .orElseThrow(() -> new ResourceNotFoundException("Location not found")));
    transportation.setType(requestDTO.getType());
    transportation.setName(requestDTO.getName());
    transportation.setPrice(requestDTO.getPrice());
    transportation.setDurationInMinutes(requestDTO.getDurationInMinutes());

    return transportation;
  }

  public Transportation toEntity(
      TransportationRequestDTO requestDTO, Transportation transportation) {
    if (requestDTO == null) {
      return transportation;
    }

    transportation.setFromLocation(
        locationRepository
            .findById(requestDTO.getFromLocationId())
            .orElseThrow(() -> new ResourceNotFoundException("Location not found")));
    transportation.setToLocation(
        locationRepository
            .findById(requestDTO.getToLocationId())
            .orElseThrow(() -> new ResourceNotFoundException("Location not found")));
    transportation.setType(requestDTO.getType());
    transportation.setName(requestDTO.getName());
    transportation.setPrice(requestDTO.getPrice());
    transportation.setDurationInMinutes(requestDTO.getDurationInMinutes());

    return transportation;
  }

  public TransportationDTO toDTO(Transportation transportation) {
    if (transportation == null) {
      return null;
    }

    TransportationDTO.TransportationDTOBuilder transportationDTO = TransportationDTO.builder();

    transportationDTO.id(transportation.getId());
    transportationDTO.name(transportation.getName());
    transportationDTO.type(transportation.getType());
    transportationDTO.fromLocation(locationMapper.toDTO(transportation.getFromLocation()));
    transportationDTO.toLocation(locationMapper.toDTO(transportation.getToLocation()));
    transportationDTO.price(transportation.getPrice());
    transportationDTO.durationInMinutes(transportation.getDurationInMinutes());

    return transportationDTO.build();
  }
}
