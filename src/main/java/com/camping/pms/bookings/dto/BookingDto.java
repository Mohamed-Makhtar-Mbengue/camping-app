package com.camping.pms.bookings.dto;

import com.camping.pms.accommodations.dto.AccommodationDto;
import com.camping.pms.bookings.Booking;
import com.camping.pms.customers.dto.CustomerDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BookingDto {
    private UUID id;
    private AccommodationDto accommodation;
    private CustomerDto customer;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer adults;
    private Integer children;
    private BigDecimal totalPrice;
    private String status;

    public static BookingDto from(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setAccommodation(AccommodationDto.from(booking.getAccommodation()));
        dto.setCustomer(CustomerDto.from(booking.getCustomer()));
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setAdults(booking.getAdults());
        dto.setChildren(booking.getChildren());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}