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
    private String type;
    private String unit;
    private String icon;
    private String imageUrl;
    private Boolean isActive;
    /** Số Villa đang sử dụng dịch vụ này */
    private Integer villaCount;

    public static ExtraServiceResponse fromEntity(ExtraService entity) {
        return ExtraServiceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .type(entity.getType())
                .unit(entity.getUnit())
                .icon(entity.getIcon())
                .imageUrl(entity.getImageUrl())
                .isActive(entity.getIsActive() != null ? entity.getIsActive() : true)
                .build();
    }

    public static ExtraServiceResponse fromEntity(ExtraService entity, int villaCount) {
        ExtraServiceResponse res = fromEntity(entity);
        res.setVillaCount(villaCount);
        return res;
    }
}
