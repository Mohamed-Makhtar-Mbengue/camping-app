package com.camping.pms.bookings;

import com.camping.pms.bookings.dto.BookingDto;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkinout")
public class CheckInOutController {

    private final BookingRepository bookingRepository;

    public CheckInOutController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/today")
    public Map<String, Object> getToday() {
        LocalDate today = LocalDate.now();

        List<BookingDto> checkIns = bookingRepository.findCheckInsToday(today)
                .stream().map(BookingDto::from).toList();

        List<BookingDto> checkOuts = bookingRepository.findCheckOutsToday(today)
                .stream().map(BookingDto::from).toList();

        List<BookingDto> present = bookingRepository.findCurrentlyPresent(today)
                .stream().map(BookingDto::from).toList();

        return Map.of(
            "date", today.toString(),
            "checkIns", checkIns,
            "checkOuts", checkOuts,
            "currentlyPresent", present,
            "totalCheckIns", checkIns.size(),
            "totalCheckOuts", checkOuts.size(),
            "totalPresent", present.size()
        );
    }

    @GetMapping("/date/{date}")
    public Map<String, Object> getByDate(@PathVariable String date) {
        LocalDate targetDate = LocalDate.parse(date);

        List<BookingDto> checkIns = bookingRepository.findCheckInsToday(targetDate)
                .stream().map(BookingDto::from).toList();

        List<BookingDto> checkOuts = bookingRepository.findCheckOutsToday(targetDate)
                .stream().map(BookingDto::from).toList();

        List<BookingDto> present = bookingRepository.findCurrentlyPresent(targetDate)
                .stream().map(BookingDto::from).toList();

        return Map.of(
            "date", targetDate.toString(),
            "checkIns", checkIns,
            "checkOuts", checkOuts,
            "currentlyPresent", present,
            "totalCheckIns", checkIns.size(),
            "totalCheckOuts", checkOuts.size(),
            "totalPresent", present.size()
        );
    }
}