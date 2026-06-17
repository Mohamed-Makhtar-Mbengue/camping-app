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

    // Nouvelles infos
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String licensePlate;
    private String secondVehicle;
    private Integer pets;
    private Integer extraPersons;
    private String vehicleType;
    private BigDecimal supplementsTotal;
    private String notes;
}