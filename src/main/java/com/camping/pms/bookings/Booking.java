package com.camping.pms.bookings;

import com.camping.pms.accommodations.Accommodation;
import com.camping.pms.customers.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer adults;
    private Integer children;
    private BigDecimal totalPrice;
    private String status;
}