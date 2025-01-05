package com.furkanbegen.routes.mapper;

import com.furkanbegen.routes.dto.RouteDTO;
import com.furkanbegen.routes.dto.TransportationDTO;
import com.furkanbegen.routes.model.Transportation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouteMapper {

  private final TransportationMapper transportationMapper;

  public RouteDTO convertToRouteDTO(List<Transportation> transportations) {
    RouteDTO routeDTO = new RouteDTO();
    routeDTO.setTransportations(toTransportationDTOList(transportations));
    calculateTotals(routeDTO);
    return routeDTO;
  }

  public List<TransportationDTO> toTransportationDTOList(List<Transportation> transportations) {
    if (transportations == null) {
      return Collections.emptyList();
    }

    List<TransportationDTO> list = new ArrayList<>();
    for (Transportation transportation : transportations) {
      list.add(transportationMapper.toDTO(transportation));
    }

    return list;
  }

  private void calculateTotals(RouteDTO routeDTO) {
    routeDTO.setTotalDuration(
        routeDTO.getTransportations().stream()
            .map(TransportationDTO::getDurationInMinutes)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .sum());

    routeDTO.setTotalPrice(
        routeDTO.getTransportations().stream()
            .map(TransportationDTO::getPrice)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .sum());
  }
}
