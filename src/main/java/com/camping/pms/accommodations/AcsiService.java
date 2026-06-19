package com.camping.pms.accommodations;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AcsiService {

    private final AcsiPeriodRepository acsiPeriodRepository;

    public AcsiService(AcsiPeriodRepository acsiPeriodRepository) {
        this.acsiPeriodRepository = acsiPeriodRepository;
    }

    public boolean isEligible(LocalDate startDate, LocalDate endDate) {
        List<AcsiPeriod> periods = acsiPeriodRepository.findAll();
        LocalDate cursor = startDate;

        while (cursor.isBefore(endDate)) {
            final LocalDate checkDate = cursor;
            boolean covered = periods.stream().anyMatch(p ->
                !checkDate.isBefore(p.getStartDate()) && !checkDate.isAfter(p.getEndDate())
            );
            if (!covered) {
                return false;
            }
            cursor = cursor.plusDays(1);
        }
        return true;
    }

    public BigDecimal calculateAcsiPrice(LocalDate startDate, LocalDate endDate) {
        List<AcsiPeriod> periods = acsiPeriodRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        LocalDate cursor = startDate;

        while (cursor.isBefore(endDate)) {
            final LocalDate checkDate = cursor;
            BigDecimal nightPrice = periods.stream()
                .filter(p -> !checkDate.isBefore(p.getStartDate()) && !checkDate.isAfter(p.getEndDate()))
                .map(AcsiPeriod::getAcsiPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);
            total = total.add(nightPrice);
            cursor = cursor.plusDays(1);
        }
        return total;
    }
}