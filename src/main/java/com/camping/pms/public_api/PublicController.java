package com.camping.pms.public_api;

import com.camping.pms.accommodations.Accommodation;
import com.camping.pms.accommodations.AccommodationRepository;
import com.camping.pms.accommodations.AcsiService;
import com.camping.pms.accommodations.PricingService;
import com.camping.pms.accommodations.dto.AccommodationDto;
import com.camping.pms.bookings.Booking;
import com.camping.pms.bookings.BookingRepository;
import com.camping.pms.customers.Customer;
import com.camping.pms.customers.CustomerRepository;
import com.camping.pms.email.PdfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final PricingService pricingService;
    private final AcsiService acsiService;
    private final PdfService pdfService;

    public PublicController(AccommodationRepository accommodationRepository,
                            BookingRepository bookingRepository,
                            CustomerRepository customerRepository,
                            PasswordEncoder passwordEncoder,
                            PricingService pricingService,
                            AcsiService acsiService,
                            PdfService pdfService) {
        this.accommodationRepository = accommodationRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.pricingService = pricingService;
        this.acsiService = acsiService;
        this.pdfService = pdfService;
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

    @GetMapping("/bookings/{id}/pdf")
    public ResponseEntity<byte[]> downloadBonEchange(@PathVariable UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        byte[] pdf = pdfService.generateBonEchange(booking);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=bon-echange-" + id + ".pdf")
                .body(pdf);
    }

    @PostMapping("/bookings")
    public Map<String, Object> createPublicBooking(@RequestBody PublicBookingRequest request) {
        Accommodation acc = accommodationRepository.findById(request.accommodationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Vérification capacité
        int totalPersons = request.adults() + request.children();
        if (totalPersons > acc.getCapacity()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Le nombre de personnes (" + totalPersons + ") dépasse la capacité maximale (" + acc.getCapacity() + " personnes)"
            );
        }

        boolean conflict = bookingRepository.existsConflict(
                request.accommodationId(),
                request.startDate(),
                request.endDate()
        );
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hébergement non disponible");
        }

        long nights = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        if (nights <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates sont invalides");
        }

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

        BigDecimal total;
        boolean acsiApplied = false;
        BigDecimal acsiDiscount = BigDecimal.ZERO;

        boolean wantsAcsi = request.hasAcsiCard() &&
                acsiService.isEligible(request.startDate(), request.endDate());

        if (wantsAcsi) {
            BigDecimal normalPrice = pricingService.calculatePrice(
                    request.accommodationId(), request.startDate(), request.endDate());
            if (normalPrice.compareTo(BigDecimal.ZERO) == 0) {
                normalPrice = acc.getBasePrice().multiply(BigDecimal.valueOf(nights));
            }
            total = acsiService.calculateAcsiPrice(request.startDate(), request.endDate());
            acsiDiscount = normalPrice.subtract(total);
            acsiApplied = true;
        } else {
            total = pricingService.calculatePrice(
                    request.accommodationId(), request.startDate(), request.endDate());
            if (total.compareTo(BigDecimal.ZERO) == 0) {
                total = acc.getBasePrice().multiply(BigDecimal.valueOf(nights));
            }
        }

        Booking booking = new Booking();
        booking.setAccommodation(acc);
        booking.setCustomer(customer);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setAdults(request.adults());
        booking.setChildren(request.children());
        booking.setTotalPrice(total);
        booking.setStatus("PENDING");
        booking.setAcsiApplied(acsiApplied);
        booking.setAcsiDiscount(acsiDiscount);
        booking.setDepositAmount(acc.getDepositRequired() != null ?
                acc.getDepositRequired() : BigDecimal.valueOf(150));
        booking.setDepositStatus("PENDING");

        // Animaux
        if (request.pets() != null) booking.setPets(request.pets());
        if (request.animalType() != null) booking.setAnimalType(request.animalType());
        if (request.animalBreed() != null) booking.setAnimalBreed(request.animalBreed());
        if (request.animalTattooed() != null) booking.setAnimalTattooed(request.animalTattooed());
        if (request.animalVaccinated() != null) booking.setAnimalVaccinated(request.animalVaccinated());

        // Véhicule
        if (request.vehicleType() != null) booking.setVehicleType(request.vehicleType());
        if (request.licensePlate() != null) booking.setLicensePlate(request.licensePlate());

        bookingRepository.save(booking);

        return Map.of(
            "bookingId", booking.getId(),
            "totalPrice", total,
            "accommodation", acc.getName(),
            "startDate", request.startDate(),
            "endDate", request.endDate(),
            "nights", nights,
            "customerEmail", customer.getEmail(),
            "acsiApplied", acsiApplied,
            "acsiDiscount", acsiDiscount
        );
    }
}