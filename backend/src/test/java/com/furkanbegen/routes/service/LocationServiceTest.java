package com.furkanbegen.routes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.furkanbegen.routes.dto.LocationDTO;
import com.furkanbegen.routes.exception.ResourceNotFoundException;
import com.furkanbegen.routes.mapper.LocationMapper;
import com.furkanbegen.routes.model.Location;
import com.furkanbegen.routes.repository.LocationRepository;
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
public class LocationServiceTest {

  public static final String ISTANBUL = "Istanbul";
  public static final String LONDON = "London";
  public static final String UPDATED_ISTANBUL = "Updated Istanbul";
  @Mock private LocationRepository locationRepository;

  @Mock private LocationMapper locationMapper;

  @InjectMocks private LocationService locationService;

  @Test
  void getAllLocations_WhenLocationsExist_ShouldReturnPageOfLocationDTOs() {
    // given
    Location location1 = new Location();
    location1.setId(1L);
    location1.setName(ISTANBUL);

    Location location2 = new Location();
    location2.setId(2L);
    location2.setName(LONDON);

    List<Location> locations = List.of(location1, location2);
    Page<Location> locationPage = new PageImpl<>(locations);

    LocationDTO locationDTO1 = new LocationDTO();
    locationDTO1.setId(1L);
    locationDTO1.setName(ISTANBUL);

    LocationDTO locationDTO2 = new LocationDTO();
    locationDTO2.setId(2L);
    locationDTO2.setName(LONDON);

    Pageable pageable = PageRequest.of(0, 10);

    when(locationRepository.findAll(pageable)).thenReturn(locationPage);
    when(locationMapper.toDTO(location1)).thenReturn(locationDTO1);
    when(locationMapper.toDTO(location2)).thenReturn(locationDTO2);

    // when
    Page<LocationDTO> result = locationService.getAllLocations(pageable);

    // then
    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    assertEquals(ISTANBUL, result.getContent().get(0).getName());
    assertEquals(LONDON, result.getContent().get(1).getName());
    verify(locationRepository).findAll(pageable);
    verify(locationMapper, times(2)).toDTO(any(Location.class));
  }

  @Test
  void getLocationById_WhenLocationExists_ShouldReturnLocationDTO() {
    // given
    Location location = new Location();
    location.setId(1L);
    location.setName(ISTANBUL);

    LocationDTO locationDTO = new LocationDTO();
    locationDTO.setId(1L);
    locationDTO.setName(ISTANBUL);

    when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
    when(locationMapper.toDTO(location)).thenReturn(locationDTO);

    // when
    LocationDTO result = locationService.getLocationById(1L);

    // then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(ISTANBUL, result.getName());
    verify(locationRepository).findById(1L);
    verify(locationMapper).toDTO(location);
  }

  @Test
  void getLocationById_WhenLocationDoesNotExist_ShouldThrowResourceNotFoundException() {
    // given
    when(locationRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationById(1L));
    verify(locationRepository).findById(1L);
    verify(locationMapper, never()).toDTO(any());
  }

  @Test
  void createLocation_WhenValidLocationDTO_ShouldReturnCreatedLocationDTO() {
    // given
    LocationDTO inputDTO = new LocationDTO();
    inputDTO.setName(ISTANBUL);
    inputDTO.setLatitude(41.0082);
    inputDTO.setLongitude(28.9784);

    Location locationToSave = new Location();
    locationToSave.setName(ISTANBUL);
    locationToSave.setLatitude(41.0082);
    locationToSave.setLongitude(28.9784);

    Location savedLocation = new Location();
    savedLocation.setId(1L);
    savedLocation.setName(ISTANBUL);
    savedLocation.setLatitude(41.0082);
    savedLocation.setLongitude(28.9784);

    LocationDTO outputDTO = new LocationDTO();
    outputDTO.setId(1L);
    outputDTO.setName(ISTANBUL);
    outputDTO.setLatitude(41.0082);
    outputDTO.setLongitude(28.9784);

    when(locationMapper.toEntity(inputDTO)).thenReturn(locationToSave);
    when(locationRepository.save(locationToSave)).thenReturn(savedLocation);
    when(locationMapper.toDTO(savedLocation)).thenReturn(outputDTO);

    // when
    LocationDTO result = locationService.createLocation(inputDTO);

    // then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(ISTANBUL, result.getName());
    assertEquals(41.0082, result.getLatitude());
    assertEquals(28.9784, result.getLongitude());
    verify(locationMapper).toEntity(inputDTO);
    verify(locationRepository).save(locationToSave);
    verify(locationMapper).toDTO(savedLocation);
  }

  @Test
  void updateLocation_WhenLocationExists_ShouldReturnUpdatedLocationDTO() {
    // given
    Long locationId = 1L;
    LocationDTO updateDTO = new LocationDTO();
    updateDTO.setName(UPDATED_ISTANBUL);
    updateDTO.setLatitude(41.0082);
    updateDTO.setLongitude(28.9784);

    Location existingLocation = new Location();
    existingLocation.setId(locationId);
    existingLocation.setName(ISTANBUL);

    Location locationToUpdate = new Location();
    locationToUpdate.setId(locationId);
    locationToUpdate.setName(UPDATED_ISTANBUL);
    locationToUpdate.setLatitude(41.0082);
    locationToUpdate.setLongitude(28.9784);

    Location updatedLocation = new Location();
    updatedLocation.setId(locationId);
    updatedLocation.setName(UPDATED_ISTANBUL);
    updatedLocation.setLatitude(41.0082);
    updatedLocation.setLongitude(28.9784);

    LocationDTO outputDTO = new LocationDTO();
    outputDTO.setId(locationId);
    outputDTO.setName(UPDATED_ISTANBUL);
    outputDTO.setLatitude(41.0082);
    outputDTO.setLongitude(28.9784);

    when(locationRepository.findById(locationId)).thenReturn(Optional.of(existingLocation));
    when(locationMapper.toEntity(updateDTO, existingLocation)).thenReturn(locationToUpdate);
    when(locationRepository.save(locationToUpdate)).thenReturn(updatedLocation);
    when(locationMapper.toDTO(updatedLocation)).thenReturn(outputDTO);

    // when
    LocationDTO result = locationService.updateLocation(locationId, updateDTO);

    // then
    assertNotNull(result);
    assertEquals(locationId, result.getId());
    assertEquals(UPDATED_ISTANBUL, result.getName());
    assertEquals(41.0082, result.getLatitude());
    assertEquals(28.9784, result.getLongitude());
    verify(locationRepository).findById(locationId);
    verify(locationMapper).toEntity(updateDTO, existingLocation);
    verify(locationRepository).save(locationToUpdate);
    verify(locationMapper).toDTO(updatedLocation);
  }

  @Test
  void updateLocation_WhenLocationDoesNotExist_ShouldThrowResourceNotFoundException() {
    // given
    Long locationId = 1L;
    LocationDTO updateDTO = new LocationDTO();
    when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(
        ResourceNotFoundException.class,
        () -> locationService.updateLocation(locationId, updateDTO));
    verify(locationRepository).findById(locationId);
    verify(locationMapper, never()).toEntity(any(), any());
    verify(locationRepository, never()).save(any());
    verify(locationMapper, never()).toDTO(any());
  }

  @Test
  void deleteLocation_WhenLocationExists_ShouldDeleteSuccessfully() {
    // given
    Long locationId = 1L;
    Location location = new Location();
    location.setId(locationId);
    when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

    // when
    locationService.deleteLocation(locationId);

    // then
    verify(locationRepository).findById(locationId);
    verify(locationRepository).delete(location);
  }

  @Test
  void deleteLocation_WhenLocationDoesNotExist_ShouldThrowResourceNotFoundException() {
    // given
    Long locationId = 1L;
    when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(ResourceNotFoundException.class, () -> locationService.deleteLocation(locationId));
    verify(locationRepository).findById(locationId);
    verify(locationRepository, never()).delete(any());
  }
}
