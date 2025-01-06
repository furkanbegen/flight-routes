package com.furkanbegen.routes.service;

import com.furkanbegen.routes.dto.TransportationDTO;
import com.furkanbegen.routes.dto.TransportationRequestDTO;
import com.furkanbegen.routes.exception.ResourceNotFoundException;
import com.furkanbegen.routes.mapper.TransportationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportationService {

  private final CacheableTransportationService cacheableTransportationService;
  private final TransportationMapper transportationMapper;

  public Page<TransportationDTO> getAllTransportations(Pageable pageable) {
    return cacheableTransportationService.findAllPaged(pageable).map(transportationMapper::toDTO);
  }

  public TransportationDTO getTransportationById(Long id) {
    var transportation =
        cacheableTransportationService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transportation not found"));
    return transportationMapper.toDTO(transportation);
  }

  public TransportationDTO createTransportation(@Valid TransportationRequestDTO requestDTO) {
    var transportation = transportationMapper.toEntity(requestDTO);
    var savedTransportation = cacheableTransportationService.save(transportation);
    return transportationMapper.toDTO(savedTransportation);
  }

  public TransportationDTO updateTransportation(
      Long id, @Valid TransportationRequestDTO requestDTO) {
    var existingTransportation =
        cacheableTransportationService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transportation not found"));

    var transportationForUpdate = transportationMapper.toEntity(requestDTO, existingTransportation);
    var updatedTransportation = cacheableTransportationService.update(transportationForUpdate);
    return transportationMapper.toDTO(updatedTransportation);
  }

  public void deleteTransportation(Long id) {
    if (!cacheableTransportationService.existsById(id)) {
      throw new ResourceNotFoundException("Transportation not found");
    }
    cacheableTransportationService.deleteById(id);
  }
}
