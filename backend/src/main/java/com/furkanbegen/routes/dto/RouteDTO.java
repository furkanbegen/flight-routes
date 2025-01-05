package com.furkanbegen.routes.dto;

import java.util.List;
import lombok.Data;

@Data
public class RouteDTO {
  private List<TransportationDTO> transportations;
  private Double totalDuration;
  private Double totalPrice;
}
