package com.furkanbegen.routes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.furkanbegen.routes.dto.LocationDTO;
import com.furkanbegen.routes.dto.TransportationDTO;
import com.furkanbegen.routes.dto.TransportationRequestDTO;
import com.furkanbegen.routes.exception.ResourceNotFoundException;
import com.furkanbegen.routes.mapper.TransportationMapper;
import com.furkanbegen.routes.model.Location;
import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.model.TransportationType;
import com.furkanbegen.routes.repository.TransportationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TransportationServiceTest {

  @Mock private TransportationRepository transportationRepository;

  @Mock private TransportationMapper transportationMapper;

  @InjectMocks private TransportationService transportationService;

  @Test
  void getAllTransportations_WhenTransportationsExist_ShouldReturnPageOfTransportationDTOs() {
    // given
    Location fromLocation = new Location();
    fromLocation.setId(1L);
    fromLocation.setName("Istanbul");
    fromLocation.setLatitude(41.0082);
    fromLocation.setLongitude(28.9784);

    Location toLocation = new Location();
    toLocation.setId(2L);
    toLocation.setName("London");
    toLocation.setLatitude(51.5074);
    toLocation.setLongitude(-0.1278);

    Transportation transportation = new Transportation();
    transportation.setId(1L);
    transportation.setName("Flight TK1234");
    transportation.setType(TransportationType.FLIGHT);
    transportation.setFromLocation(fromLocation);
    transportation.setToLocation(toLocation);
    transportation.setPrice(500.0);
    transportation.setDurationInMinutes(240.0);

    TransportationDTO transportationDTO =
        new TransportationDTO(
            1L,
            "Flight TK1234",
            TransportationType.FLIGHT,
            new LocationDTO(1L, "Istanbul", 41.0082, 28.9784),
            new LocationDTO(2L, "London", 51.5074, -0.1278),
            500.0,
            240.0);

    List<Transportation> transportations = List.of(transportation);
    Page<Transportation> transportationPage = new PageImpl<>(transportations);
    Pageable pageable = PageRequest.of(0, 10);

    when(transportationRepository.findAll(pageable)).thenReturn(transportationPage);
    when(transportationMapper.toDTO(transportation)).thenReturn(transportationDTO);

    // when
    Page<TransportationDTO> result = transportationService.getAllTransportations(pageable);

    // then
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    TransportationDTO resultDTO = result.getContent().get(0);
    assertEquals(transportationDTO.getId(), resultDTO.getId());
    assertEquals(transportationDTO.getName(), resultDTO.getName());
    assertEquals(transportationDTO.getType(), resultDTO.getType());
    assertEquals(transportationDTO.getPrice(), resultDTO.getPrice());
    assertEquals(transportationDTO.getDurationInMinutes(), resultDTO.getDurationInMinutes());

    verify(transportationRepository).findAll(pageable);
    verify(transportationMapper).toDTO(transportation);
  }

  @Test
  void getTransportationById_WhenTransportationExists_ShouldReturnTransportationDTO() {
    // given
    Location fromLocation = new Location();
    fromLocation.setId(1L);
    fromLocation.setName("Istanbul");
    fromLocation.setLatitude(41.0082);
    fromLocation.setLongitude(28.9784);

    Location toLocation = new Location();
    toLocation.setId(2L);
    toLocation.setName("London");
    toLocation.setLatitude(51.5074);
    toLocation.setLongitude(-0.1278);

    Transportation transportation = new Transportation();
    transportation.setId(1L);
    transportation.setName("Flight TK1234");
    transportation.setType(TransportationType.FLIGHT);
    transportation.setFromLocation(fromLocation);
    transportation.setToLocation(toLocation);
    transportation.setPrice(500.0);
    transportation.setDurationInMinutes(240.0);

    TransportationDTO transportationDTO =
        new TransportationDTO(
            1L,
            "Flight TK1234",
            TransportationType.FLIGHT,
            new LocationDTO(1L, "Istanbul", 41.0082, 28.9784),
            new LocationDTO(2L, "London", 51.5074, -0.1278),
            500.0,
            240.0);

    when(transportationRepository.findById(1L)).thenReturn(Optional.of(transportation));
    when(transportationMapper.toDTO(transportation)).thenReturn(transportationDTO);

    // when
    TransportationDTO result = transportationService.getTransportationById(1L);

    // then
    assertNotNull(result);
    assertEquals(transportationDTO.getId(), result.getId());
    assertEquals(transportationDTO.getName(), result.getName());
    assertEquals(transportationDTO.getType(), result.getType());
    assertEquals(transportationDTO.getPrice(), result.getPrice());
    assertEquals(transportationDTO.getDurationInMinutes(), result.getDurationInMinutes());

    verify(transportationRepository).findById(1L);
    verify(transportationMapper).toDTO(transportation);
  }

  @Test
  void getTransportationById_WhenTransportationDoesNotExist_ShouldThrowResourceNotFoundException() {
    // given
    when(transportationRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(
        ResourceNotFoundException.class, () -> transportationService.getTransportationById(1L));
    verify(transportationRepository).findById(1L);
    verify(transportationMapper, never()).toDTO(any());
  }

  @Test
  void createTransportation_WhenValidRequest_ShouldReturnCreatedTransportationDTO() {
    // given
    Location fromLocation = new Location();
    fromLocation.setId(1L);
    fromLocation.setName("Istanbul");
    fromLocation.setLatitude(41.0082);
    fromLocation.setLongitude(28.9784);

    Location toLocation = new Location();
    toLocation.setId(2L);
    toLocation.setName("London");
    toLocation.setLatitude(51.5074);
    toLocation.setLongitude(-0.1278);

    TransportationRequestDTO requestDTO = new TransportationRequestDTO();
    requestDTO.setName("Flight TK1234");
    requestDTO.setType(TransportationType.FLIGHT);
    requestDTO.setFromLocationId(1L);
    requestDTO.setToLocationId(2L);
    requestDTO.setPrice(500.0);
    requestDTO.setDurationInMinutes(240.0);

    Transportation transportation = new Transportation();
    transportation.setId(1L);
    transportation.setName("Flight TK1234");
    transportation.setType(TransportationType.FLIGHT);
    transportation.setFromLocation(fromLocation);
    transportation.setToLocation(toLocation);
    transportation.setPrice(500.0);
    transportation.setDurationInMinutes(240.0);

    TransportationDTO transportationDTO =
        new TransportationDTO(
            1L,
            "Flight TK1234",
            TransportationType.FLIGHT,
            new LocationDTO(1L, "Istanbul", 41.0082, 28.9784),
            new LocationDTO(2L, "London", 51.5074, -0.1278),
            500.0,
            240.0);

    when(transportationMapper.toEntity(requestDTO)).thenReturn(transportation);
    when(transportationRepository.save(transportation)).thenReturn(transportation);
    when(transportationMapper.toDTO(transportation)).thenReturn(transportationDTO);

    // when
    TransportationDTO result = transportationService.createTransportation(requestDTO);

    // then
    assertNotNull(result);
    assertEquals(transportation.getId(), result.getId());
    assertEquals(transportation.getName(), result.getName());
    assertEquals(transportation.getType(), result.getType());
    assertEquals(transportation.getPrice(), result.getPrice());
    assertEquals(transportation.getDurationInMinutes(), result.getDurationInMinutes());

    verify(transportationMapper).toEntity(requestDTO);
    verify(transportationRepository).save(transportation);
    verify(transportationMapper).toDTO(transportation);
  }

  @Test
  void updateTransportation_WhenTransportationExists_ShouldReturnUpdatedTransportationDTO() {
    // given
    Location fromLocation = new Location();
    fromLocation.setId(1L);
    fromLocation.setName("Istanbul");
    fromLocation.setLatitude(41.0082);
    fromLocation.setLongitude(28.9784);

    Location toLocation = new Location();
    toLocation.setId(2L);
    toLocation.setName("London");
    toLocation.setLatitude(51.5074);
    toLocation.setLongitude(-0.1278);

    Transportation existingTransportation = new Transportation();
    existingTransportation.setId(1L);
    existingTransportation.setName("Flight TK1234");
    existingTransportation.setType(TransportationType.FLIGHT);
    existingTransportation.setFromLocation(fromLocation);
    existingTransportation.setToLocation(toLocation);
    existingTransportation.setPrice(500.0);
    existingTransportation.setDurationInMinutes(240.0);

    TransportationRequestDTO requestDTO = new TransportationRequestDTO();
    requestDTO.setName("Updated Flight");
    requestDTO.setType(TransportationType.FLIGHT);
    requestDTO.setFromLocationId(1L);
    requestDTO.setToLocationId(2L);
    requestDTO.setPrice(600.0);
    requestDTO.setDurationInMinutes(250.0);

    Transportation updatedTransportation = new Transportation();
    updatedTransportation.setId(1L);
    updatedTransportation.setName("Updated Flight");
    updatedTransportation.setType(TransportationType.FLIGHT);
    updatedTransportation.setFromLocation(fromLocation);
    updatedTransportation.setToLocation(toLocation);
    updatedTransportation.setPrice(600.0);
    updatedTransportation.setDurationInMinutes(250.0);

    TransportationDTO updatedDTO =
        new TransportationDTO(
            1L,
            "Updated Flight",
            TransportationType.FLIGHT,
            new LocationDTO(1L, "Istanbul", 41.0082, 28.9784),
            new LocationDTO(2L, "London", 51.5074, -0.1278),
            600.0,
            250.0);

    when(transportationRepository.findById(1L)).thenReturn(Optional.of(existingTransportation));
    when(transportationMapper.toEntity(requestDTO, existingTransportation))
        .thenReturn(updatedTransportation);
    when(transportationRepository.save(updatedTransportation)).thenReturn(updatedTransportation);
    when(transportationMapper.toDTO(updatedTransportation)).thenReturn(updatedDTO);

    // when
    TransportationDTO result = transportationService.updateTransportation(1L, requestDTO);

    // then
    assertNotNull(result);
    assertEquals(updatedTransportation.getId(), result.getId());
    assertEquals(updatedTransportation.getName(), result.getName());
    assertEquals(updatedTransportation.getType(), result.getType());
    assertEquals(updatedTransportation.getPrice(), result.getPrice());
    assertEquals(updatedTransportation.getDurationInMinutes(), result.getDurationInMinutes());

    verify(transportationRepository).findById(1L);
    verify(transportationMapper).toEntity(requestDTO, existingTransportation);
    verify(transportationRepository).save(updatedTransportation);
    verify(transportationMapper).toDTO(updatedTransportation);
  }

  @Test
  void updateTransportation_WhenTransportationDoesNotExist_ShouldThrowResourceNotFoundException() {
    // given
    TransportationRequestDTO requestDTO = new TransportationRequestDTO();
    requestDTO.setName("Updated Flight");
    when(transportationRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(
        ResourceNotFoundException.class,
        () -> transportationService.updateTransportation(1L, requestDTO));

    verify(transportationRepository).findById(1L);
    verify(transportationMapper, never()).toEntity(any(), any());
    verify(transportationRepository, never()).save(any());
    verify(transportationMapper, never()).toDTO(any());
  }

  @Test
  void deleteTransportation_WhenTransportationExists_ShouldDeleteSuccessfully() {
    // given
    Transportation transportation = new Transportation();
    transportation.setId(1L);
    transportation.setName("Flight TK1234");

    when(transportationRepository.findById(1L)).thenReturn(Optional.of(transportation));

    // when
    transportationService.deleteTransportation(1L);

    // then
    verify(transportationRepository).findById(1L);
    verify(transportationRepository).delete(transportation);
  }

  @Test
  void deleteTransportation_WhenTransportationDoesNotExist_ShouldThrowResourceNotFoundException() {
    // given
    when(transportationRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(
        ResourceNotFoundException.class, () -> transportationService.deleteTransportation(1L));

    verify(transportationRepository).findById(1L);
    verify(transportationRepository, never()).delete(any());
  }
}
