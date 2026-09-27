package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.InventoryTransaction;
import com.phungvanlong.booking_hotel.entity.InventoryTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private InventoryTransactionType type;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String reason;
    private String performer;
    private Long destinationVillaId;
    private String destinationVillaNumber;
    private LocalDateTime createdAt;

    public static InventoryTransactionResponse fromEntity(InventoryTransaction tx) {
        if (tx == null) return null;

        Long itemId = null;
        String itemCode = null;
        String itemName = null;
        if (tx.getItem() != null) {
            itemId = tx.getItem().getId();
            itemCode = tx.getItem().getCode();
            itemName = tx.getItem().getName();
        }

        Long destVillaId = null;
        String destVillaNumber = null;
        if (tx.getDestinationVilla() != null) {
            destVillaId = tx.getDestinationVilla().getId();
            destVillaNumber = tx.getDestinationVilla().getVillaNumber();
            if (tx.getDestinationVilla().getZone() != null) {
                destVillaNumber += " (" + tx.getDestinationVilla().getZone().getName() + ")";
            }
        }

        return InventoryTransactionResponse.builder()
                .id(tx.getId())
                .itemId(itemId)
                .itemCode(itemCode)
                .itemName(itemName)
                .type(tx.getType())
                .quantity(tx.getQuantity())
                .unitPrice(tx.getUnitPrice())
                .totalAmount(tx.getTotalAmount())
                .reason(tx.getReason())
                .performer(tx.getPerformer())
                .destinationVillaId(destVillaId)
                .destinationVillaNumber(destVillaNumber)
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
