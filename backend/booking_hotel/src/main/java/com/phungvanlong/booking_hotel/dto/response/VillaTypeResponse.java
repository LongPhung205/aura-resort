package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.VillaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaTypeResponse implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal dynamicPrice;
    private Boolean isDynamicPricingEnabled;
    private Integer capacity;
    private Integer adults;
    private Integer children;
    private String bedType;
    private String imageUrl;
    private List<String> images;
    private Integer totalVillas;
    private Double averageRating;
    private Integer totalReviews;

    public static VillaTypeResponse fromEntity(VillaType entity) {
        if (entity == null) return null;

        int ad = entity.getAdults() != null ? entity.getAdults() : (entity.getCapacity() != null ? entity.getCapacity() : 2);
        int ch = entity.getChildren() != null ? entity.getChildren() : 0;
        int cap = entity.getCapacity() != null ? entity.getCapacity() : (ad + ch);

        return VillaTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice())
                .dynamicPrice(entity.getDynamicPrice())
                .isDynamicPricingEnabled(entity.getIsDynamicPricingEnabled())
                .capacity(cap)
                .adults(ad)
                .children(ch)
                .bedType(entity.getBedType())
                .imageUrl(entity.getImageUrl())
                .totalVillas(entity.getVillas() != null ? entity.getVillas().size() : 0)
                .images(new ArrayList<>())
                .build();
    }
}
