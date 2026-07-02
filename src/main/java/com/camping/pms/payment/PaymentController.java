package com.camping.pms.payment;

import com.camping.pms.bookings.Booking;
import com.camping.pms.bookings.BookingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    public PaymentController(PaymentService paymentService, BookingRepository bookingRepository) {
        this.paymentService = paymentService;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping("/create-intent")
    public Map<String, String> createIntent(@RequestBody CreateIntentRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));

        try {
            // Acompte = 30% du prix total (ajustable)
            BigDecimal depositAmount = booking.getTotalPrice()
                    .multiply(BigDecimal.valueOf(0.30))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            PaymentIntent intent = paymentService.createPaymentIntent(
                    depositAmount,
                    booking.getId().toString(),
                    booking.getCustomer().getEmail()
            );

            booking.setPaymentIntentId(intent.getId());
            booking.setPaymentStatus("PROCESSING");
            bookingRepository.save(booking);

            return Map.of(
                "clientSecret", intent.getClientSecret(),
                "amount", depositAmount.toString()
            );
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur Stripe: " + e.getMessage());
        }
    }

    public record CreateIntentRequest(UUID bookingId) {}
}