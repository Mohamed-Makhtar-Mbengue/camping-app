package com.camping.pms.public_api;

import com.camping.pms.accommodations.Accommodation;
import com.camping.pms.accommodations.AccommodationRepository;
import com.camping.pms.accommodations.dto.AccommodationDto;
import com.camping.pms.bookings.Booking;
import com.camping.pms.bookings.BookingRepository;
import com.camping.pms.customers.Customer;
import com.camping.pms.customers.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final AccommodationRepository accommodationRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public PublicController(AccommodationRepository accommodationRepository,
                            BookingRepository bookingRepository,
                            CustomerRepository customerRepository,
                            PasswordEncoder passwordEncoder) {
        this.accommodationRepository = accommodationRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/accommodations")
    public Page<AccommodationDto> getAccommodations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        return accommodationRepository.findAll(
            PageRequest.of(page, size, Sort.by("name"))
        ).map(AccommodationDto::from);
    }

    @GetMapping("/accommodations/{id}")
    public AccommodationDto getAccommodation(@PathVariable UUID id) {
        return AccommodationDto.from(accommodationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/accommodations/{id}/availability")
    public Map<String, Object> checkAvailability(
            @PathVariable UUID id,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        boolean available = !bookingRepository.existsConflict(
                id,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
        return Map.of("available", available);
    }

    @PostMapping("/bookings")
    public Map<String, Object> createPublicBooking(@RequestBody PublicBookingRequest request) {
        Accommodation acc = accommodationRepository.findById(request.accommodationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        boolean conflict = bookingRepository.existsConflict(
                request.accommodationId(),
                request.startDate(),
                request.endDate()
        );
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hébergement non disponible");
        }

        // Créer ou trouver le customer
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    Customer c = new Customer();
                    c.setFirstName(request.firstName());
                    c.setLastName(request.lastName());
                    c.setEmail(request.email());
                    c.setPhone(request.phone());
                    c.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return customerRepository.save(c);
                });

        long nights = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        BigDecimal total = acc.getBasePrice().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setAccommodation(acc);
        booking.setCustomer(customer);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setAdults(request.adults());
        booking.setChildren(request.children());
        booking.setTotalPrice(total);
        booking.setStatus("PENDING");
        bookingRepository.save(booking);

        return Map.of(
            "bookingId", booking.getId(),
            "totalPrice", total,
            "accommodation", acc.getName(),
            "startDate", request.startDate(),
            "endDate", request.endDate(),
            "nights", nights,
            "customerEmail", customer.getEmail()
        );
    }
}