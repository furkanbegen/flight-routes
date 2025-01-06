package com.furkanbegen.routes.controller;

import static com.furkanbegen.routes.constant.AppConstant.API_BASE_PATH;

import com.furkanbegen.routes.dto.LocationDTO;
import com.furkanbegen.routes.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_BASE_PATH + "/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  @GetMapping
  public ResponseEntity<Page<LocationDTO>> getAllLocations(@PageableDefault Pageable pageable) {
    return ResponseEntity.ok(locationService.getAllLocations(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<LocationDTO> getLocationById(@PathVariable(name = "id") Long id) {
    return ResponseEntity.ok(locationService.getLocationById(id));
  }

  @PostMapping
  public ResponseEntity<LocationDTO> createLocation(@RequestBody @Valid LocationDTO locationDTO) {
    return ResponseEntity.ok(locationService.createLocation(locationDTO));
  }

  @PutMapping("/{id}")
  public ResponseEntity<LocationDTO> updateLocation(
      @PathVariable(name = "id") Long id, @RequestBody @Valid LocationDTO locationDTO) {
    return ResponseEntity.ok(locationService.updateLocation(id, locationDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLocation(@PathVariable(name = "id") Long id) {
    locationService.deleteLocation(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public Page<LocationDTO> searchLocations(
      @RequestParam String query,
      @PageableDefault Pageable pageable) {
    return locationService.searchLocations(query, pageable);
  }
}
