package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.InventoryTransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionRequest {

    @NotNull(message = "ID vật tư không được để trống")
    private Long itemId;

    @NotNull(message = "Loại giao dịch (IMPORT, EXPORT, ADJUSTMENT) không được để trống")
    private InventoryTransactionType type;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng giao dịch phải từ 1 trở lên")
    private Integer quantity;

    @Min(value = 0, message = "Đơn giá không được âm")
    private BigDecimal unitPrice;

    private String reason;

    private String performer;

    private Long destinationVillaId;
}
