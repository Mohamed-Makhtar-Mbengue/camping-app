package com.camping.pms.accommodations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AcsiPeriodRepository extends JpaRepository<AcsiPeriod, UUID> {
    List<AcsiPeriod> findAll();
}