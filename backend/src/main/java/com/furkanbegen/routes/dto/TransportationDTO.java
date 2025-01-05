package com.furkanbegen.routes.dto;

import com.furkanbegen.routes.model.TransportationType;
import lombok.*;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportationDTO {

  private Long id;
  private String name;
  private TransportationType type;
  private LocationDTO fromLocation;
  private LocationDTO toLocation;
  private Double price;
  private Double durationInMinutes;
}
