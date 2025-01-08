package com.furkanbegen.routes.service;

import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.repository.TransportationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Service
@RequiredArgsConstructor
public class CacheableTransportationService {

  private final TransportationRepository transportationRepository;
  private final CacheManager cacheManager;

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

  public Transportation save(Transportation transportation) {
    Transportation saved = transportationRepository.save(transportation);
    updateCache(saved);
    return saved;
  }

  public Transportation update(Transportation transportation) {
    Transportation updated = transportationRepository.save(transportation);
    updateCache(updated);
    return updated;
  }

  @CacheEvict(value = "transportations", allEntries = true)
  public void deleteById(Long id) {
    transportationRepository.deleteById(id);
    removeFromCache(id);
  }

  private void removeFromCache(Long transportationId) {
    Cache cache = cacheManager.getCache("transportations");
    if (cache != null) {
      @SuppressWarnings("unchecked")
      List<Transportation> cachedTransportations = (List<Transportation>) cache.get("transportations", List.class);
      if (cachedTransportations != null) {
        // Remove the transportation with the given id
        cachedTransportations.removeIf(t -> t.getId().equals(transportationId));
        cache.put("transportations", cachedTransportations);
      }
    }
  }

  private void updateCache(Transportation transportation) {
    Cache cache = cacheManager.getCache("transportations");
    if (cache != null) {
      @SuppressWarnings("unchecked")
      List<Transportation> cachedTransportations = (List<Transportation>) cache.get("transportations", List.class);
      if (cachedTransportations != null) {
        // Remove old version if exists
        cachedTransportations.removeIf(t -> t.getId().equals(transportation.getId()));
        // Add new/updated transportation
        cachedTransportations.add(transportation);
        cache.put("transportations", cachedTransportations);
      }
    }
  }
}
