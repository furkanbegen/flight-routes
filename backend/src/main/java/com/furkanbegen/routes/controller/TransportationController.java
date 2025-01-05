package com.furkanbegen.routes.controller;

import static com.furkanbegen.routes.constant.AppConstant.API_BASE_PATH;

import com.furkanbegen.routes.dto.TransportationDTO;
import com.furkanbegen.routes.dto.TransportationRequestDTO;
import com.furkanbegen.routes.service.TransportationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_BASE_PATH + "/transportations")
@RequiredArgsConstructor
public class TransportationController {

  private final TransportationService transportationService;

  @GetMapping
  public ResponseEntity<Page<TransportationDTO>> getAllTransportations(
      @PageableDefault Pageable pageable) {
    return ResponseEntity.ok(transportationService.getAllTransportations(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransportationDTO> getTransportationById(
      @PathVariable(name = "id") Long id) {
    return ResponseEntity.ok(transportationService.getTransportationById(id));
  }

  @PostMapping
  public ResponseEntity<TransportationDTO> createTransportation(
      @RequestBody @Valid TransportationRequestDTO requestDTO) {
    return ResponseEntity.ok(transportationService.createTransportation(requestDTO));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TransportationDTO> updateTransportation(
      @PathVariable(name = "id") Long id, @RequestBody @Valid TransportationRequestDTO requestDTO) {
    return ResponseEntity.ok(transportationService.updateTransportation(id, requestDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTransportation(@PathVariable(name = "id") Long id) {
    transportationService.deleteTransportation(id);
    return ResponseEntity.noContent().build();
  }
}
