package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.VillaSupplyStandard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaSupplyStandardResponse {

    private Long id;
    private Long villaId;
    private String villaNumber;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String itemUnit;
    private InventoryCategory itemCategory;
    private BigDecimal itemUnitPrice;
    private Integer standardQuantity;
    private String note;

    public static VillaSupplyStandardResponse fromEntity(VillaSupplyStandard entity) {
        if (entity == null) return null;

        String vNumber = entity.getVilla() != null ? entity.getVilla().getVillaNumber() : null;
        if (entity.getVilla() != null && entity.getVilla().getZone() != null) {
            vNumber += " (" + entity.getVilla().getZone().getName() + ")";
        }

        Long itemId = null;
        String itemCode = null;
        String itemName = null;
        String itemUnit = null;
        InventoryCategory cat = null;
        BigDecimal price = null;

        if (entity.getItem() != null) {
            itemId = entity.getItem().getId();
            itemCode = entity.getItem().getCode();
            itemName = entity.getItem().getName();
            itemUnit = entity.getItem().getUnit();
            cat = entity.getItem().getCategory();
            price = entity.getItem().getUnitPrice();
        }

        return VillaSupplyStandardResponse.builder()
                .id(entity.getId())
                .villaId(entity.getVilla() != null ? entity.getVilla().getId() : null)
                .villaNumber(vNumber)
                .itemId(itemId)
                .itemCode(itemCode)
                .itemName(itemName)
                .itemUnit(itemUnit)
                .itemCategory(cat)
                .itemUnitPrice(price)
                .standardQuantity(entity.getStandardQuantity())
                .note(entity.getNote())
                .build();
    }
}
