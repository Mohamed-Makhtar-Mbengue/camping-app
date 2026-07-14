package com.camping.pms.bookings;

import com.camping.pms.bookings.dto.BookingDto;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final BookingRepository bookingRepository;

    public CalendarController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/month/{year}/{month}")
    public Map<String, Object> getMonthCalendar(
            @PathVariable int year,
            @PathVariable int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<BookingDto> bookings = bookingRepository
                .findAllByMonth(startOfMonth, endOfMonth)
                .stream().map(BookingDto::from).toList();

        return Map.of(
            "year", year,
            "month", month,
            "startOfMonth", startOfMonth.toString(),
            "endOfMonth", endOfMonth.toString(),
            "bookings", bookings,
            "totalBookings", bookings.size()
        );
    }

    @GetMapping("/accommodation/{id}/month/{year}/{month}")
    public Map<String, Object> getAccommodationCalendar(
            @PathVariable UUID id,
            @PathVariable int year,
            @PathVariable int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<BookingDto> bookings = bookingRepository
                .findByAccommodationAndMonth(id, startOfMonth, endOfMonth)
                .stream().map(BookingDto::from).toList();

        return Map.of(
            "accommodationId", id.toString(),
            "year", year,
            "month", month,
            "bookings", bookings
        );
    }
}