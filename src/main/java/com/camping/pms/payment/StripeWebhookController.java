package com.camping.pms.payment;

import com.camping.pms.bookings.Booking;
import com.camping.pms.bookings.BookingRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/public/payment/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    private final BookingRepository bookingRepository;

    public StripeWebhookController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature invalide");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElse(null);
            if (intent != null) {
                String bookingId = intent.getMetadata().get("bookingId");
                bookingRepository.findById(UUID.fromString(bookingId)).ifPresent(booking -> {
                    booking.setPaymentStatus("SUCCEEDED");
                    booking.setDepositPaidAmount(BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100)));
                    bookingRepository.save(booking);
                });
            }
        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElse(null);
            if (intent != null) {
                String bookingId = intent.getMetadata().get("bookingId");
                bookingRepository.findById(UUID.fromString(bookingId)).ifPresent(booking -> {
                    booking.setPaymentStatus("FAILED");
                    bookingRepository.save(booking);
                });
            }
        }

        return ResponseEntity.ok("");
    }
}