package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.VillaService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaServiceResponse {
    private Long id;
    private Long villaId;
    private String villaNumber;
    private Long serviceId;
    private String serviceName;
    private String serviceType;
    private String serviceIcon;
    private String serviceUnit;
    private BigDecimal defaultPrice;
    private BigDecimal priceOverride;
    /** Giá thực tế áp dụng: priceOverride nếu có, không thì defaultPrice */
    private BigDecimal effectivePrice;
    private Boolean isAvailable;
    private String note;

    public static VillaServiceResponse fromEntity(VillaService entity) {
        BigDecimal defPrice = entity.getService().getPrice();
        BigDecimal override = entity.getPriceOverride();
        BigDecimal effective = override != null ? override : defPrice;

        return VillaServiceResponse.builder()
                .id(entity.getId())
                .villaId(entity.getVilla().getId())
                .villaNumber(entity.getVilla().getVillaNumber())
                .serviceId(entity.getService().getId())
                .serviceName(entity.getService().getName())
                .serviceType(entity.getService().getType())
                .serviceIcon(entity.getService().getIcon())
                .serviceUnit(entity.getService().getUnit())
                .defaultPrice(defPrice)
                .priceOverride(override)
                .effectivePrice(effective)
                .isAvailable(entity.getIsAvailable() != null ? entity.getIsAvailable() : true)
                .note(entity.getNote())
                .build();
    }
}
