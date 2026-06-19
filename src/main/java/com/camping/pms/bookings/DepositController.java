package com.camping.pms.bookings;

import com.camping.pms.bookings.dto.BookingDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings/{id}/deposit")
public class DepositController {

    private final BookingRepository bookingRepository;

    public DepositController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @PatchMapping("/hold")
    public BookingDto holdDeposit(@PathVariable UUID id, @RequestParam BigDecimal amount) {
        Booking booking = getBooking(id);
        booking.setDepositAmount(amount);
        booking.setDepositStatus("HELD");
        return BookingDto.from(bookingRepository.save(booking));
    }

    @PatchMapping("/return")
    public BookingDto returnDeposit(@PathVariable UUID id) {
        Booking booking = getBooking(id);
        booking.setDepositStatus("RETURNED");
        booking.setDepositReturnedDate(LocalDate.now());
        booking.setDepositDeduction(BigDecimal.ZERO);
        return BookingDto.from(bookingRepository.save(booking));
    }

    @PatchMapping("/partial-retain")
    public BookingDto partialRetain(
            @PathVariable UUID id,
            @RequestParam BigDecimal deduction,
            @RequestParam String reason) {
        Booking booking = getBooking(id);
        if (booking.getDepositAmount() == null || deduction.compareTo(booking.getDepositAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Montant de retenue invalide");
        }
        booking.setDepositStatus("PARTIALLY_RETAINED");
        booking.setDepositDeduction(deduction);
        booking.setDepositDeductionReason(reason);
        booking.setDepositReturnedDate(LocalDate.now());
        return BookingDto.from(bookingRepository.save(booking));
    }

    private Booking getBooking(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));
    }
}