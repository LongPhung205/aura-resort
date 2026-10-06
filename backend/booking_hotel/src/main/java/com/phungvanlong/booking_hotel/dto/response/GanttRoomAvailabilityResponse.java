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
public class GanttRoomAvailabilityResponse implements Serializable {
    private String villaNumber; // e.g. "Villa #801"
    private String roomTypeName; // e.g. "Grand Oceanfront Pool"
    private String zoneName;
    private String statusTag; 
    private String statusTagClass;
    private List<GanttDaySlot> daySlots;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GanttDaySlot implements Serializable {
        private String dateLabel; // "H.Nay (18/4)", "T7 (19/4)", etc.
        private String status; // "OCCUPIED", "CONFIRMED", "AVAILABLE", "DEEP_CLEAN", "MAINTENANCE"
        private String guestName;
        private Integer spanDays; // how many days this block spans
        private Boolean isSpanStart;
        private String blockLabel; // "Trịnh Gia Bảo (Diamond) - 2 Đêm"
        private String colorClass;
        private Long bookingId;
        private String bookingCode;
    }
}
