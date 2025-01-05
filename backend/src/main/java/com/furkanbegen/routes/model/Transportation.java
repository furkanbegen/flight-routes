package com.furkanbegen.routes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "transportations")
@EqualsAndHashCode(callSuper = true)
public class Transportation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "from_location_id")
  private Location fromLocation;

  @ManyToOne
  @JoinColumn(name = "to_location_id")
  private Location toLocation;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private TransportationType type;

  private String name;

  private Double price;

  private Double durationInMinutes;
}
