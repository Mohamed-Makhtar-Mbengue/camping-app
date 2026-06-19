package com.camping.pms.accommodations.dto;

import com.camping.pms.accommodations.Accommodation;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccommodationDto {
    private UUID id;
    private String name;
    private String type;
    private String category;
    private Integer capacity;
    private Integer surface;
    private BigDecimal basePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal depositRequired;
    private String description;

    public static AccommodationDto from(Accommodation accommodation) {
        AccommodationDto dto = new AccommodationDto();
        dto.setId(accommodation.getId());
        dto.setName(accommodation.getName());
        dto.setType(accommodation.getType());
        dto.setCategory(accommodation.getCategory());
        dto.setCapacity(accommodation.getCapacity());
        dto.setSurface(accommodation.getSurface());
        dto.setBasePrice(accommodation.getBasePrice());
        dto.setMinPrice(accommodation.getMinPrice());
        dto.setMaxPrice(accommodation.getMaxPrice());
        dto.setDepositRequired(accommodation.getDepositRequired());
        dto.setDescription(accommodation.getDescription());
        return dto;
    }
}