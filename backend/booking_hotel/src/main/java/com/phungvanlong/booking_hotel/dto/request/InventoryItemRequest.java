package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class InventoryItemRequest {

    private String code;

    @NotBlank(message = "Tên vật tư không được để trống")
    private String name;

    @NotNull(message = "Phân loại vật tư không được để trống")
    private InventoryCategory category;

    private String unit;

    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer inStock;

    @Min(value = 0, message = "Định mức tối thiểu không được âm")
    private Integer minThreshold;

    @Min(value = 0, message = "Đơn giá không được âm")
    private BigDecimal unitPrice;

    private String supplier;

    private String location;

    private String description;
}
