package com.camping.pms.accommodations;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "accommodations")
public class Accommodation {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;      // ex : Emplacement 12
    private String type;      // ex : TENTE, MOBIL_HOME
    private Integer capacity; // nb de personnes

    private BigDecimal basePrice; // prix par nuit

    private String description;

    // getters / setters
}
