package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffRosterResponse {
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private Integer totalStaff;
    private Integer onDutyToday;
    private Integer lateToday;
    private Integer onLeaveToday;
    private List<StaffWeeklyScheduleItem> staffMembers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffWeeklyScheduleItem {
        private Long staffId;
        private String fullName;
        private String role;
        private String department;
        private String avatarUrl;
        private List<DayScheduleItem> days;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayScheduleItem {
        private LocalDate date;
        private String shiftName; // Ca Sáng, Ca Chiều, Ca Đêm, Nghỉ
        private String shiftColor;
        private String status; // SCHEDULED, PRESENT, LATE, OFF
    }
}
