package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.ShiftSwapRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSwapResponse {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private String requesterRole;
    private Long targetStaffId;
    private String targetStaffName;
    private LocalDate targetDate;
    private String currentShiftName;
    private String desiredShiftName;
    private String requestType; // SWAP, OVERTIME, LEAVE
    private String reason;
    private String status;
    private String approverName;
    private LocalDateTime actionAt;
    private LocalDateTime createdAt;

    public static ShiftSwapResponse fromEntity(ShiftSwapRequest req) {
        return ShiftSwapResponse.builder()
                .id(req.getId())
                .requesterId(req.getRequester() != null ? req.getRequester().getId() : null)
                .requesterName(req.getRequester() != null ? req.getRequester().getFullName() : null)
                .requesterRole(req.getRequester() != null && req.getRequester().getRole() != null ? req.getRequester().getRole().name() : null)
                .targetStaffId(req.getTargetStaff() != null ? req.getTargetStaff().getId() : null)
                .targetStaffName(req.getTargetStaff() != null ? req.getTargetStaff().getFullName() : null)
                .targetDate(req.getTargetDate())
                .currentShiftName(req.getCurrentShift() != null ? req.getCurrentShift().getName() : null)
                .desiredShiftName(req.getDesiredShift() != null ? req.getDesiredShift().getName() : null)
                .requestType(req.getRequestType())
                .reason(req.getReason())
                .status(req.getStatus())
                .approverName(req.getApprover() != null ? req.getApprover().getFullName() : null)
                .actionAt(req.getActionAt())
                .createdAt(req.getCreatedAt())
                .build();
    }
}
