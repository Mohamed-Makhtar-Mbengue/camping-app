package com.camping.pms.accommodations;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "accommodations")
public class Accommodation {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String type;
    private Integer capacity;
    private BigDecimal basePrice;
    private String description;
}