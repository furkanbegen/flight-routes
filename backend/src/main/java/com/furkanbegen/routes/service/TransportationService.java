package com.furkanbegen.routes.service;

import com.furkanbegen.routes.dto.TransportationDTO;
import com.furkanbegen.routes.dto.TransportationRequestDTO;
import com.furkanbegen.routes.exception.ResourceNotFoundException;
import com.furkanbegen.routes.mapper.TransportationMapper;
import com.furkanbegen.routes.repository.TransportationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportationService {

  private final TransportationRepository transportationRepository;
  private final TransportationMapper transportationMapper;

  public Page<TransportationDTO> getAllTransportations(Pageable pageable) {
    return transportationRepository.findAll(pageable).map(transportationMapper::toDTO);
  }

  public TransportationDTO getTransportationById(Long id) {
    var transportation =
        transportationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transportation not found"));

    return transportationMapper.toDTO(transportation);
  }

  public TransportationDTO createTransportation(@Valid TransportationRequestDTO requestDTO) {
    var transportation = transportationMapper.toEntity(requestDTO);

    return transportationMapper.toDTO(transportationRepository.save(transportation));
  }

  public TransportationDTO updateTransportation(
      Long id, @Valid TransportationRequestDTO requestDTO) {

    var transportation =
        transportationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transportation not found"));

    var transportationForUpdate = transportationMapper.toEntity(requestDTO, transportation);

    return transportationMapper.toDTO(transportationRepository.save(transportationForUpdate));
  }

  public void deleteTransportation(Long id) {
    var transportation =
        transportationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transportation not found"));

    transportationRepository.delete(transportation);
  }
}
