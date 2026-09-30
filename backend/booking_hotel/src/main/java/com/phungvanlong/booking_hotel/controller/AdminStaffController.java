package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.BiometricCheckInRequest;
import com.phungvanlong.booking_hotel.dto.request.ShiftSwapActionRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.AttendanceLogResponse;
import com.phungvanlong.booking_hotel.dto.response.ShiftSwapResponse;
import com.phungvanlong.booking_hotel.dto.response.StaffRosterResponse;
import com.phungvanlong.booking_hotel.service.AdminStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final AdminStaffService staffService;

    @GetMapping("/roster")
    public ResponseEntity<ApiResponse<StaffRosterResponse>> getRoster(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        StaffRosterResponse roster = staffService.getWeeklyRoster(startDate);
        return ResponseEntity.ok(ApiResponse.success(roster, "Lấy bảng phân ca Live Shift tuần thành công"));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<List<AttendanceLogResponse>>> getAttendance() {
        List<AttendanceLogResponse> logs = staffService.getRecentAttendance();
        return ResponseEntity.ok(ApiResponse.success(logs, "Lấy lịch sử chấm công FaceID/GPS thành công"));
    }

    @PostMapping("/biometric-checkin")
    public ResponseEntity<ApiResponse<AttendanceLogResponse>> biometricCheckIn(
            @Valid @RequestBody BiometricCheckInRequest request) {
        AttendanceLogResponse log = staffService.recordBiometricCheckIn(request);
        return ResponseEntity.ok(ApiResponse.success(log, "Ghi nhận chấm công sinh trắc học thành công"));
    }

    @GetMapping("/swap-requests")
    public ResponseEntity<ApiResponse<List<ShiftSwapResponse>>> getSwapRequests() {
        List<ShiftSwapResponse> requests = staffService.getSwapRequests();
        return ResponseEntity.ok(ApiResponse.success(requests, "Lấy danh sách yêu cầu đổi ca thành công"));
    }

    @PostMapping("/swap-requests/action")
    public ResponseEntity<ApiResponse<ShiftSwapResponse>> processSwap(
            @Valid @RequestBody ShiftSwapActionRequest request,
            Authentication authentication) {
        String approverEmail = authentication != null ? authentication.getName() : null;
        ShiftSwapResponse response = staffService.processSwapRequest(request, approverEmail);
        return ResponseEntity.ok(ApiResponse.success(response, "Xử lý yêu cầu đổi ca/OT thành công"));
    }
}
