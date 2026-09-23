package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.AttendanceLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLogResponse {
    private Long id;
    private Long staffId;
    private String staffName;
    private String department;
    private LocalDateTime checkTime;
    private String checkType;
    private String method;
    private String locationName;
    private Double matchAccuracy;
    private String photoUrl;
    private String status;

    public static AttendanceLogResponse fromEntity(AttendanceLog log) {
        return AttendanceLogResponse.builder()
                .id(log.getId())
                .staffId(log.getStaff() != null ? log.getStaff().getId() : null)
                .staffName(log.getStaff() != null ? log.getStaff().getFullName() : null)
                .department(log.getLocationName())
                .checkTime(log.getCheckTime())
                .checkType(log.getCheckType())
                .method(log.getMethod())
                .locationName(log.getLocationName())
                .matchAccuracy(log.getMatchAccuracy())
                .photoUrl(log.getPhotoUrl())
                .status(log.getStatus())
                .build();
    }
}
