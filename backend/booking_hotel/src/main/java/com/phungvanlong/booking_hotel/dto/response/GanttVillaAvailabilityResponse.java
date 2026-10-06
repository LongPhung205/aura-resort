package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GanttVillaAvailabilityResponse implements Serializable {
    private String villaNumber; // e.g. "Villa #801", "Pine-01"
    private String villaTypeName; // e.g. "Grand Oceanfront Pool Villa"
    private String zone; // e.g. "Khu A - Biển Đông"
    private String statusTag; 
    private String statusTagClass;
    private List<GanttDaySlot> daySlots;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GanttDaySlot implements Serializable {
        private String dateLabel; // "H.Nay (18/4)", "T7 (19/4)", etc.
        private String status; // "OCCUPIED", "CONFIRMED", "AVAILABLE", "CLEANING", "MAINTENANCE"
        private String guestName;
        private Integer spanDays;
        private Boolean isSpanStart;
        private String blockLabel;
        private String colorClass;
        private Long bookingId;
        private String bookingCode;
    }

    // Backward compatibility getter
    public String getRoomTypeName() {
        return villaTypeName;
    }
}
