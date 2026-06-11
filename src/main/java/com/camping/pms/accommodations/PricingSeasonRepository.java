package com.camping.pms.accommodations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PricingSeasonRepository extends JpaRepository<PricingSeason, UUID> {
    List<PricingSeason> findByAccommodationId(UUID accommodationId);
}