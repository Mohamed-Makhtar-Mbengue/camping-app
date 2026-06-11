package com.camping.pms.accommodations;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PricingService {

    private final PricingSeasonRepository pricingSeasonRepository;

    public PricingService(PricingSeasonRepository pricingSeasonRepository) {
        this.pricingSeasonRepository = pricingSeasonRepository;
    }

    public BigDecimal calculatePrice(UUID accommodationId, LocalDate startDate, LocalDate endDate) {
        List<PricingSeason> seasons = pricingSeasonRepository.findByAccommodationId(accommodationId);
        BigDecimal total = BigDecimal.ZERO;
        LocalDate current = startDate;

        while (current.isBefore(endDate)) {
            BigDecimal nightPrice = getPriceForDate(seasons, current);
            total = total.add(nightPrice);
            current = current.plusDays(1);
        }
        return total;
    }

    private BigDecimal getPriceForDate(List<PricingSeason> seasons, LocalDate date) {
        return seasons.stream()
            .filter(s -> !date.isBefore(s.getStartDate()) && !date.isAfter(s.getEndDate()))
            .map(PricingSeason::getPricePerNight)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }
}