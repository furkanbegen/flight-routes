package com.furkanbegen.routes.repository;

import com.furkanbegen.routes.model.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportationRepository extends JpaRepository<Transportation, Long> {}
