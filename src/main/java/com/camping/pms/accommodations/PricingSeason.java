package com.camping.pms.accommodations;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "pricing_seasons")
public class PricingSeason {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal pricePerNight;
}