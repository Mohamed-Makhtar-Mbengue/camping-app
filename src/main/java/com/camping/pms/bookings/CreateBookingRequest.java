package com.camping.pms.bookings;

import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingRequest(
    UUID accommodationId,
    LocalDate startDate,
    LocalDate endDate,
    int adults,
    int children,
    boolean hasAcsiCard
) {}