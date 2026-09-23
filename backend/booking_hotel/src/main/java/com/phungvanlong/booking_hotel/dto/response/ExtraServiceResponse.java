package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.ExtraService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraServiceResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    public static ExtraServiceResponse fromEntity(ExtraService entity) {
        return ExtraServiceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .build();
    }
}
