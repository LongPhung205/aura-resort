package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.WeeklyShiftRegistration;
import com.phungvanlong.booking_hotel.entity.WeeklyShiftRegistrationDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyShiftRegistrationResponse {

    private Long id;
    private Long staffId;
    private String staffName;
    private String staffRole;
    private String department;
    private String avatarUrl;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private String status; // PENDING, APPROVED, REJECTED
    private String preferredZone;
    private String notes;
    private String approverName;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private List<RegistrationDayResponse> days;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationDayResponse {
        private Long id;
        private LocalDate workDate;
        private String dayOfWeek;
        private String shiftType;
        private String note;
    }

    public static WeeklyShiftRegistrationResponse fromEntity(WeeklyShiftRegistration reg) {
        if (reg == null) return null;

        List<RegistrationDayResponse> dayList = new ArrayList<>();
        if (reg.getDetails() != null) {
            dayList = reg.getDetails().stream()
                    .map(d -> RegistrationDayResponse.builder()
                            .id(d.getId())
                            .workDate(d.getWorkDate())
                            .dayOfWeek(d.getDayOfWeek())
                            .shiftType(d.getShiftType())
                            .note(d.getNote())
                            .build())
                    .collect(Collectors.toList());
        }

        String staffDept = "Bộ Phận Vận Hành";
        if (reg.getStaff() != null && reg.getStaff().getRole() != null) {
            switch (reg.getStaff().getRole().name()) {
                case "ROLE_BUTLER": staffDept = "Quản Gia Butler VIP"; break;
                case "ROLE_RECEPTIONIST": staffDept = "Lễ Tân & Đón Tiếp"; break;
                case "ROLE_HOUSEKEEPING": staffDept = "Buồng Phòng & Ozone"; break;
                case "ROLE_ACCOUNTANT": staffDept = "Tài Chính & Kế Toán"; break;
            }
        }

        return WeeklyShiftRegistrationResponse.builder()
                .id(reg.getId())
                .staffId(reg.getStaff() != null ? reg.getStaff().getId() : null)
                .staffName(reg.getStaff() != null ? reg.getStaff().getFullName() : "Nhân viên")
                .staffRole(reg.getStaff() != null && reg.getStaff().getRole() != null ? reg.getStaff().getRole().name() : "")
                .department(staffDept)
                .avatarUrl(reg.getStaff() != null ? reg.getStaff().getAvatar() : null)
                .weekStartDate(reg.getWeekStartDate())
                .weekEndDate(reg.getWeekEndDate())
                .status(reg.getStatus())
                .preferredZone(reg.getPreferredZone())
                .notes(reg.getNotes())
                .approverName(reg.getApprover() != null ? reg.getApprover().getFullName() : null)
                .approvedAt(reg.getApprovedAt())
                .rejectionReason(reg.getRejectionReason())
                .createdAt(reg.getCreatedAt())
                .days(dayList)
                .build();
    }
}
