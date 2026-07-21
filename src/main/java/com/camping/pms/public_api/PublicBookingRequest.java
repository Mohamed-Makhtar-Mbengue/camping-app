package com.camping.pms.public_api;

import java.time.LocalDate;
import java.util.UUID;

public record PublicBookingRequest(
    UUID accommodationId,
    LocalDate startDate,
    LocalDate endDate,
    int adults,
    int children,
    String firstName,
    String lastName,
    String email,
    String phone,
    boolean hasAcsiCard,
    // Animaux
    Integer pets,
    String animalType,
    String animalBreed,
    Boolean animalTattooed,
    Boolean animalVaccinated,
    // Véhicule
    String vehicleType,
    String licensePlate
) {}