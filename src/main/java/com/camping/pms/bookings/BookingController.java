package com.camping.pms.bookings;

import com.camping.pms.accommodations.Accommodation;
import com.camping.pms.accommodations.AccommodationRepository;
import com.camping.pms.customers.Customer;
import com.camping.pms.customers.CustomerRepository;
import org.springframework.web.bind.annotation.*;

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

    public BookingController(BookingRepository bookingRepository,
                             AccommodationRepository accommodationRepository,
                             CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.accommodationRepository = accommodationRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @PostMapping
    public Booking create(@RequestBody CreateBookingRequest request) {
        Accommodation acc = accommodationRepository.findById(request.accommodationId())
                .orElseThrow();
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow();

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

        return bookingRepository.save(booking);
    }
}
