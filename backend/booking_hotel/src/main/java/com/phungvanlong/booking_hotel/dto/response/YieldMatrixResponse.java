package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YieldMatrixResponse implements Serializable {
    private Double currentOccupancy;
    private Double predictedWeekendOccupancy;
    private Boolean isAiDynamicPricingActive;
    private String yieldStrategy; // AGGRESSIVE, BALANCED, PRESERVATION
    private List<VillaTypeYieldItem> villaTypes;

    // Backward compatibility getter/setter
    public List<VillaTypeYieldItem> getRoomTypes() {
        return villaTypes;
    }

    public void setRoomTypes(List<VillaTypeYieldItem> roomTypes) {
        this.villaTypes = roomTypes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VillaTypeYieldItem implements Serializable {
        private Long villaTypeId;
        private String villaTypeName;
        private BigDecimal basePrice;
        private BigDecimal dynamicPrice;
        private Double occupancyRate;
        private Double suggestedAdjustmentPercent;
        private String demandLevel; // PEAK, HIGH, NORMAL, LOW

        // Backward compatibility getters
        public Long getRoomTypeId() {
            return villaTypeId;
        }

        public String getRoomTypeName() {
            return villaTypeName;
        }
    }

    // Alias class for legacy code
    public static class RoomTypeYieldItem extends VillaTypeYieldItem {}
}
