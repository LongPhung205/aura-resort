package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.InventoryItem;
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
public class InventoryItemResponse {

    private Long id;
    private String code;
    private String name;
    private InventoryCategory category;
    private String categoryLabel;
    private String unit;
    private Integer inStock;
    private Integer minThreshold;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private String supplier;
    private String location;
    private String description;
    private boolean isLowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryItemResponse fromEntity(InventoryItem item) {
        if (item == null) return null;

        BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        int stock = item.getInStock() != null ? item.getInStock() : 0;
        BigDecimal total = price.multiply(BigDecimal.valueOf(stock));

        int min = item.getMinThreshold() != null ? item.getMinThreshold() : 0;
        boolean lowStock = stock <= min;

        String label = switch (item.getCategory()) {
            case AMENITY -> "Tiêu hao buồng phòng (Amenities)";
            case LINEN -> "Đồ vải / Khăn ga (Linen)";
            case MINIBAR -> "Minibar / Đồ uống F&B";
            case CLEANING -> "Hóa chất & Vệ sinh";
            case EQUIPMENT -> "Thiết bị & Dụng cụ";
            case OTHER -> "Vật tư khác";
        };

        return InventoryItemResponse.builder()
                .id(item.getId())
                .code(item.getCode())
                .name(item.getName())
                .category(item.getCategory())
                .categoryLabel(label)
                .unit(item.getUnit())
                .inStock(stock)
                .minThreshold(min)
                .unitPrice(price)
                .totalValue(total)
                .supplier(item.getSupplier())
                .location(item.getLocation())
                .description(item.getDescription())
                .isLowStock(lowStock)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
