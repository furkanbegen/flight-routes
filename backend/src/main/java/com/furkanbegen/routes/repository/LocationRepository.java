package com.furkanbegen.routes.repository;

import com.furkanbegen.routes.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
  @Query("SELECT l FROM Location l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%'))")
  Page<Location> searchByNameContainingIgnoreCase(@Param("query") String query, Pageable pageable);
}
