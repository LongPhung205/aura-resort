package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryResponse {

    private long totalItems;
    private long lowStockCount;
    private BigDecimal totalInventoryValue;
    private long totalTransactions;
    private long totalVillaAssets;
}
