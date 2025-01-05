package com.furkanbegen.routes.mapper;

import com.furkanbegen.routes.dto.LocationDTO;
import com.furkanbegen.routes.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

  public Location toEntity(LocationDTO locationDTO) {
    if (locationDTO == null) {
      return null;
    }

    Location location = new Location();

    location.setName(locationDTO.getName());
    location.setLatitude(locationDTO.getLatitude());
    location.setLongitude(locationDTO.getLongitude());

    return location;
  }

  public Location toEntity(LocationDTO locationDTO, Location location) {
    if (locationDTO == null) {
      return location;
    }

    location.setName(locationDTO.getName());
    location.setLatitude(locationDTO.getLatitude());
    location.setLongitude(locationDTO.getLongitude());

    return location;
  }

  public LocationDTO toDTO(Location location) {
    if (location == null) {
      return null;
    }

    LocationDTO.LocationDTOBuilder locationDTO = LocationDTO.builder();

    locationDTO.id(location.getId());
    locationDTO.name(location.getName());
    locationDTO.latitude(location.getLatitude());
    locationDTO.longitude(location.getLongitude());

    return locationDTO.build();
  }
}
