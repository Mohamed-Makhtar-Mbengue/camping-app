package com.camping.pms.bookings;

import com.camping.pms.accommodations.Accommodation;
import com.camping.pms.accommodations.AccommodationRepository;
import com.camping.pms.accommodations.dto.AccommodationDto;
import com.camping.pms.bookings.dto.BookingDto;
import com.camping.pms.email.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings/{id}/assignment")
public class AssignmentController {

    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final EmailService emailService;

    public AssignmentController(BookingRepository bookingRepository,
                                AccommodationRepository accommodationRepository,
                                EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.accommodationRepository = accommodationRepository;
        this.emailService = emailService;
    }

    @GetMapping("/suggestions")
    public List<AccommodationDto> getSuggestions(@PathVariable UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String category = booking.getAccommodation().getCategory();
        int minCapacity = booking.getAdults() + (booking.getChildren() != null ? booking.getChildren() : 0);

        return bookingRepository.findAvailableByCategory(
                category,
                minCapacity,
                booking.getStartDate(),
                booking.getEndDate()
        ).stream().map(AccommodationDto::from).toList();
    }

    @PatchMapping("/assign")
    public BookingDto assignAndConfirm(
            @PathVariable UUID id,
            @RequestParam UUID accommodationId) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Accommodation newAccommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emplacement non trouvé"));

        booking.setAccommodation(newAccommodation);
        booking.setStatus("CONFIRMED");
        Booking saved = bookingRepository.save(booking);

        try {
            emailService.sendConfirmationEmail(saved);
        } catch (Exception e) {
            System.err.println("Erreur envoi email: " + e.getMessage());
        }

        return BookingDto.from(saved);
    }
}