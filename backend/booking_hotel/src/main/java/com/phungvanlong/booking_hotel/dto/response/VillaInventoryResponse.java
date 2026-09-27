package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.VillaInventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaInventoryResponse {

    private Long id;
    private Long villaId;
    private String villaNumber;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String itemUnit;
    private InventoryCategory itemCategory;
    private Integer standardQuantity;
    private Integer currentQuantity;
    private Integer deficitQuantity;
    private int fillPercentage;
    private LocalDateTime lastCheckedAt;

    public static VillaInventoryResponse fromEntity(VillaInventory entity, Integer standardQty) {
        if (entity == null) return null;

        String vNumber = entity.getVilla() != null ? entity.getVilla().getVillaNumber() : null;
        if (entity.getVilla() != null && entity.getVilla().getZone() != null) {
            vNumber += " (" + entity.getVilla().getZone().getName() + ")";
        }

        int std = standardQty != null ? standardQty : 0;
        int curr = entity.getCurrentQuantity() != null ? entity.getCurrentQuantity() : 0;
        int deficit = Math.max(0, std - curr);
        int pct = (std > 0) ? Math.min(100, Math.round(((float) curr / std) * 100)) : 100;

        return VillaInventoryResponse.builder()
                .id(entity.getId())
                .villaId(entity.getVilla() != null ? entity.getVilla().getId() : null)
                .villaNumber(vNumber)
                .itemId(entity.getItem() != null ? entity.getItem().getId() : null)
                .itemCode(entity.getItem() != null ? entity.getItem().getCode() : null)
                .itemName(entity.getItem() != null ? entity.getItem().getName() : null)
                .itemUnit(entity.getItem() != null ? entity.getItem().getUnit() : null)
                .itemCategory(entity.getItem() != null ? entity.getItem().getCategory() : null)
                .standardQuantity(std)
                .currentQuantity(curr)
                .deficitQuantity(deficit)
                .fillPercentage(pct)
                .lastCheckedAt(entity.getLastCheckedAt())
                .build();
    }
}
