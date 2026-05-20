package com.camping.pms.accommodations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccommodationRepository extends JpaRepository<Accommodation, UUID> {
}
