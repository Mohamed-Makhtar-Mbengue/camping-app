package com.camping.pms.bookings;

import com.camping.pms.customers.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByCustomer(Customer customer);

    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.accommodation.id = :accommodationId
        AND b.status != 'CANCELLED'
        AND b.startDate < :endDate
        AND b.endDate > :startDate
    """)
    boolean existsConflict(
        @Param("accommodationId") UUID accommodationId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}