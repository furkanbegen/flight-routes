package com.furkanbegen.routes.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class LocationDTO {

  private Long id;

  @NotEmpty(message = "Location name is required")
  private String name;

  private Double latitude;
  private Double longitude;
}
