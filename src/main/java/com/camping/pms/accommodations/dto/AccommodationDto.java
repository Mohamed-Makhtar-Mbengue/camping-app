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
    private Integer capacity;
    private BigDecimal basePrice;
    private String description;

    public static AccommodationDto from(Accommodation accommodation) {
        AccommodationDto dto = new AccommodationDto();
        dto.setId(accommodation.getId());
        dto.setName(accommodation.getName());
        dto.setType(accommodation.getType());
        dto.setCapacity(accommodation.getCapacity());
        dto.setBasePrice(accommodation.getBasePrice());
        dto.setDescription(accommodation.getDescription());
        return dto;
    }
}