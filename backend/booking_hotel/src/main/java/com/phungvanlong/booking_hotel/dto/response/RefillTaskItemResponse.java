package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.RefillTaskItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefillTaskItemResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String itemUnit;
    private InventoryCategory itemCategory;
    private BigDecimal unitPrice;
    private Integer standardQuantity;
    private Integer actualQuantity;
    private Integer refillQuantity;
    private Integer consumedQuantity;
    private Integer damagedQuantity;
    private Integer missingQuantity;
    private Boolean isFulfilled;
    private Integer warehouseStock; // Số lượng tồn kho trung tâm hiện tại

    public static RefillTaskItemResponse fromEntity(RefillTaskItem entity) {
        if (entity == null) return null;

        Long itemId = null;
        String itemCode = null;
        String itemName = null;
        String itemUnit = null;
        InventoryCategory cat = null;
        BigDecimal price = null;
        Integer inStock = null;

        if (entity.getItem() != null) {
            itemId = entity.getItem().getId();
            itemCode = entity.getItem().getCode();
            itemName = entity.getItem().getName();
            itemUnit = entity.getItem().getUnit();
            cat = entity.getItem().getCategory();
            price = entity.getItem().getUnitPrice();
            inStock = entity.getItem().getInStock();
        }

        return RefillTaskItemResponse.builder()
                .id(entity.getId())
                .itemId(itemId)
                .itemCode(itemCode)
                .itemName(itemName)
                .itemUnit(itemUnit)
                .itemCategory(cat)
                .unitPrice(price)
                .standardQuantity(entity.getStandardQuantity())
                .actualQuantity(entity.getActualQuantity())
                .refillQuantity(entity.getRefillQuantity())
                .consumedQuantity(entity.getConsumedQuantity())
                .damagedQuantity(entity.getDamagedQuantity())
                .missingQuantity(entity.getMissingQuantity())
                .isFulfilled(entity.getIsFulfilled())
                .warehouseStock(inStock)
                .build();
    }
}
