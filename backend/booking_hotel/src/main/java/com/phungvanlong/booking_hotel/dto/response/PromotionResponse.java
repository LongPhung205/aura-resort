package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.DiscountType;
import com.phungvanlong.booking_hotel.entity.Promotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer quantity;
    private String name;
    private String category;

    public static PromotionResponse fromEntity(Promotion entity) {
        return PromotionResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .quantity(entity.getQuantity())
                .name(entity.getName())
                .category(entity.getCategory())
                .build();
    }
}
