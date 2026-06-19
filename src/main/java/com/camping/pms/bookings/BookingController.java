package com.camping.pms.bookings;

import com.camping.pms.accommodations.Accommodation;
import com.camping.pms.accommodations.AccommodationRepository;
import com.camping.pms.accommodations.PricingService;
import com.camping.pms.bookings.dto.BookingDto;
import com.camping.pms.customers.Customer;
import com.camping.pms.customers.CustomerRepository;
import com.camping.pms.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.camping.pms.email.EmailService;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final CustomerRepository customerRepository;
    private final CurrentUserService currentUserService;
    private final PricingService pricingService;
    private final EmailService emailService;

    public BookingController(BookingRepository bookingRepository,
                             AccommodationRepository accommodationRepository,
                             CustomerRepository customerRepository,
                             CurrentUserService currentUserService,
                             PricingService pricingService, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.accommodationRepository = accommodationRepository;
        this.customerRepository = customerRepository;
        this.currentUserService = currentUserService;
        this.pricingService = pricingService;
        this.emailService = emailService;
    }

    @GetMapping
    public List<BookingDto> findAll() {
        return bookingRepository.findAll().stream()
                .map(BookingDto::from)
                .toList();
    }

    @GetMapping("/my")
    public List<BookingDto> findMine() {
        Customer currentUser = currentUserService.getCurrentUser();
        return bookingRepository.findByCustomer(currentUser).stream()
                .map(BookingDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public BookingDto findById(@PathVariable UUID id) {
        return BookingDto.from(bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée")));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestBody CreateBookingRequest request) {
        Customer currentUser = currentUserService.getCurrentUser();

        Accommodation acc = accommodationRepository.findById(request.accommodationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hébergement non trouvé"));

        long nights = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        if (nights <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates sont invalides");
        }

        boolean conflict = bookingRepository.existsConflict(
                request.accommodationId(),
                request.startDate(),
                request.endDate()
        );
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hébergement non disponible pour ces dates");
        }

        // Calcul prix dynamique selon saison
        BigDecimal total = pricingService.calculatePrice(
                request.accommodationId(),
                request.startDate(),
                request.endDate()
        );

        // Si pas de saison définie, utilise le basePrice
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            total = acc.getBasePrice().multiply(BigDecimal.valueOf(nights));
        }

        Booking booking = new Booking();
        booking.setAccommodation(acc);
        booking.setCustomer(currentUser);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setAdults(request.adults());
        booking.setChildren(request.children());
        booking.setTotalPrice(total);
        booking.setStatus("PENDING");

        return BookingDto.from(bookingRepository.save(booking));
    }

    @PatchMapping("/{id}/status")
    public BookingDto updateStatus(@PathVariable UUID id, @RequestParam String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));
        booking.setStatus(status);
        Booking saved = bookingRepository.save(booking);

        // Envoie l'email si confirmation
        if ("CONFIRMED".equals(status)) {
            try {
                emailService.sendConfirmationEmail(saved);
            } catch (Exception e) {
                // Log l'erreur mais ne bloque pas la confirmation
                System.err.println("Erreur envoi email: " + e.getMessage());
            }
        }
        return BookingDto.from(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée");
        }
        bookingRepository.deleteById(id);
    }
}