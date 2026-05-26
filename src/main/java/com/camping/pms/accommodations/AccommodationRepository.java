package com.camping.pms.accommodations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AccommodationRepository extends JpaRepository<Accommodation, UUID> {
    Page<Accommodation> findAll(Pageable pageable);
}