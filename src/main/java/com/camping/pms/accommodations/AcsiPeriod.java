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
@Table(name = "acsi_periods")
public class AcsiPeriod {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal acsiPrice;
}