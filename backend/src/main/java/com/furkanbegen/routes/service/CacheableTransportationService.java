package com.furkanbegen.routes.service;

import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheableTransportationService {

  private final TransportationRepository transportationRepository;

  @Cacheable("transportations")
  public List<Transportation> findAll() {
    return transportationRepository.findAll();
  }

  public Page<Transportation> findAllPaged(Pageable pageable) {
    return transportationRepository.findAll(pageable);
  }

  public Optional<Transportation> findById(Long id) {
    return transportationRepository.findById(id);
  }

  public boolean existsById(Long id) {
    return transportationRepository.existsById(id);
  }

  @CacheEvict(value = "transportations", allEntries = true)
  public Transportation save(Transportation transportation) {
    return transportationRepository.save(transportation);
  }

  @CacheEvict(value = "transportations", allEntries = true)
  public void deleteById(Long id) {
    transportationRepository.deleteById(id);
  }

  @CacheEvict(value = "transportations", allEntries = true)
  public Transportation update(Transportation transportation) {
    return transportationRepository.save(transportation);
  }
}
