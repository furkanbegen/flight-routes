package com.furkanbegen.routes.dto;

import com.furkanbegen.routes.model.TransportationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransportationRequestDTO {

  @NotEmpty(message = "Transportation name is required")
  private String name;

  @NotNull(message = "Transportation type is required")
  private TransportationType type;

  @NotNull(message = "From location id is required")
  private Long fromLocationId;

  @NotNull(message = "To location id is required")
  private Long toLocationId;

  private Double price;

  private Double durationInMinutes;
}
