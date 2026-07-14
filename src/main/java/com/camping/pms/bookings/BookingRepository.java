package com.camping.pms.bookings;

import com.camping.pms.accommodations.Accommodation;
import com.camping.pms.customers.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByCustomer(Customer customer);

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status != 'CANCELLED'")
    BigDecimal sumTotalPrice();

    @Query("SELECT COALESCE(SUM(b.adults + b.children), 0) FROM Booking b WHERE b.status != 'CANCELLED'")
    int sumTotalPersons();

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

    @Query("""
        SELECT new map(
            a.name as accommodationName,
            a.type as accommodationType,
            COUNT(b) as totalBookings,
            COALESCE(SUM(b.adults + b.children), 0) as totalPersons,
            COALESCE(SUM(b.totalPrice), 0) as totalRevenue
        )
        FROM Booking b
        JOIN b.accommodation a
        WHERE b.status != 'CANCELLED'
        GROUP BY a.id, a.name, a.type
        ORDER BY totalBookings DESC
    """)
    List<Map<String, Object>> countBookingsByAccommodation();

    @Query("""
        SELECT a FROM Accommodation a
        WHERE a.category = :category
        AND a.capacity >= :minCapacity
        AND a.id NOT IN (
            SELECT b.accommodation.id FROM Booking b
            WHERE b.status != 'CANCELLED'
            AND b.startDate < :endDate
            AND b.endDate > :startDate
        )
        ORDER BY a.capacity ASC
    """)
    List<Accommodation> findAvailableByCategory(
        @Param("category") String category,
        @Param("minCapacity") int minCapacity,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT b FROM Booking b
    WHERE b.startDate = :today
    AND b.status = 'CONFIRMED'
    ORDER BY b.accommodation.name ASC
    """)
    List<Booking> findCheckInsToday(@Param("today") LocalDate today);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.endDate = :today
        AND b.status = 'CONFIRMED'
        ORDER BY b.accommodation.name ASC
    """)
    List<Booking> findCheckOutsToday(@Param("today") LocalDate today);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.startDate <= :today
        AND b.endDate > :today
        AND b.status = 'CONFIRMED'
        ORDER BY b.accommodation.name ASC
    """)
    List<Booking> findCurrentlyPresent(@Param("today") LocalDate today);

    @Query("""
    SELECT b FROM Booking b
    WHERE b.accommodation.id = :accommodationId
    AND b.status != 'CANCELLED'
    AND b.startDate <= :endOfMonth
    AND b.endDate >= :startOfMonth
    """)
    List<Booking> findByAccommodationAndMonth(
        @Param("accommodationId") UUID accommodationId,
        @Param("startOfMonth") LocalDate startOfMonth,
        @Param("endOfMonth") LocalDate endOfMonth
    );

    @Query("""
        SELECT b FROM Booking b
        WHERE b.status != 'CANCELLED'
        AND b.startDate <= :endOfMonth
        AND b.endDate >= :startOfMonth
        ORDER BY b.startDate ASC
    """)
    List<Booking> findAllByMonth(
        @Param("startOfMonth") LocalDate startOfMonth,
        @Param("endOfMonth") LocalDate endOfMonth
    );
}