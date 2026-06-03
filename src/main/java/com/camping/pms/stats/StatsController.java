package com.camping.pms.stats;

import com.camping.pms.accommodations.AccommodationRepository;
import com.camping.pms.bookings.BookingRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

        stats.put("totalAccommodations", totalAccommodations);
        stats.put("totalBookings", totalBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("confirmedBookings", confirmedBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        stats.put("totalPersons", totalPersons);

        return stats;
    }

    @GetMapping("/by-accommodation")
    public List<Map<String, Object>> getStatsByAccommodation() {
        return bookingRepository.countBookingsByAccommodation();
    }
}