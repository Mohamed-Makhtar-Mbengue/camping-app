package com.camping.pms.stats;

import com.camping.pms.accommodations.AccommodationRepository;
import com.camping.pms.bookings.BookingRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final AccommodationRepository accommodationRepository;
    private final BookingRepository bookingRepository;

    public StatsController(AccommodationRepository accommodationRepository,
                           BookingRepository bookingRepository) {
        this.accommodationRepository = accommodationRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalAccommodations = accommodationRepository.count();
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus("PENDING");
        long confirmedBookings = bookingRepository.countByStatus("CONFIRMED");
        long cancelledBookings = bookingRepository.countByStatus("CANCELLED");

        BigDecimal totalRevenue = bookingRepository.sumTotalPrice();
        int totalPersons = bookingRepository.sumTotalPersons();

        // Taux d'occupation du mois courant
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        int daysInMonth = currentMonth.lengthOfMonth();

        long occupiedDays = bookingRepository.countOccupiedDays(startOfMonth, endOfMonth);
        long totalPossibleDays = totalAccommodations * daysInMonth;
        double occupancyRate = totalPossibleDays > 0
            ? (double) occupiedDays / totalPossibleDays * 100
            : 0;

        // Revenus du mois courant
        BigDecimal monthRevenue = bookingRepository.sumRevenueByPeriod(startOfMonth, endOfMonth);

        // Revenus par mois (6 derniers mois)
        List<Map<String, Object>> revenueByMonth = bookingRepository.getRevenueByMonth();

        stats.put("totalAccommodations", totalAccommodations);
        stats.put("totalBookings", totalBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("confirmedBookings", confirmedBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        stats.put("totalPersons", totalPersons);
        stats.put("occupancyRate", Math.round(occupancyRate * 10.0) / 10.0);
        stats.put("monthRevenue", monthRevenue != null ? monthRevenue : BigDecimal.ZERO);
        stats.put("revenueByMonth", revenueByMonth);

        return stats;
    }

    @GetMapping("/by-accommodation")
    public List<Map<String, Object>> getStatsByAccommodation() {
        return bookingRepository.countBookingsByAccommodation();
    }

    @GetMapping("/occupancy/{year}/{month}")
    public Map<String, Object> getOccupancyByMonth(
            @PathVariable int year,
            @PathVariable int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        int daysInMonth = yearMonth.lengthOfMonth();
        long totalAccommodations = accommodationRepository.count();

        long occupiedDays = bookingRepository.countOccupiedDays(startOfMonth, endOfMonth);
        long totalPossibleDays = totalAccommodations * daysInMonth;
        double occupancyRate = totalPossibleDays > 0
            ? (double) occupiedDays / totalPossibleDays * 100
            : 0;

        BigDecimal revenue = bookingRepository.sumRevenueByPeriod(startOfMonth, endOfMonth);

        return Map.of(
            "year", year,
            "month", month,
            "occupancyRate", Math.round(occupancyRate * 10.0) / 10.0,
            "occupiedDays", occupiedDays,
            "totalPossibleDays", totalPossibleDays,
            "revenue", revenue != null ? revenue : BigDecimal.ZERO
        );
    }
}