package com.camping.pms.accommodations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
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
    private String category;
    private Integer capacity;
    private Integer surface;
    private BigDecimal basePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL)
    private List<PricingSeason> pricingSeasons;
}