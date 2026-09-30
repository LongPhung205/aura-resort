package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.RoomConsumptionItemType;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRoomInspectionRequest {

    @Builder.Default
    private List<MinibarItemInspectionDto> minibarItems = new ArrayList<>();

    @Builder.Default
    private Boolean amenitiesComplimentaryConfirmed = true;

    @Builder.Default
    private List<AssetIncidentReportDto> damagedOrLostAssets = new ArrayList<>();

    private String note;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MinibarItemInspectionDto {
        private Long inventoryItemId;
        private String itemName;
        private Integer standardQuantity;
        private Integer currentQuantity;
        private BigDecimal unitPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetIncidentReportDto {
        private Long inventoryItemId;
        private String itemName;
        private RoomConsumptionItemType incidentType; // ASSET_DAMAGED, ASSET_LOST
        private Integer quantity;
        private BigDecimal compensationPrice;
        private String evidencePhotoUrl;
        private String note;
    }
}
