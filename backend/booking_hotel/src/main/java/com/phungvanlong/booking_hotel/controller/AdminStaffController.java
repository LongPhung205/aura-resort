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

    @PostMapping("/schedule/cell")
    public ResponseEntity<ApiResponse<Void>> updateScheduleCell(
            @Valid @RequestBody com.phungvanlong.booking_hotel.dto.request.UpdateScheduleCellRequest request) {
        staffService.updateScheduleCell(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật ca trực thành công"));
    }

    @PostMapping("/schedule/ai-generate")
    public ResponseEntity<ApiResponse<StaffRosterResponse>> generateAiWeeklyRoster(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        StaffRosterResponse roster = staffService.generateAiWeeklyRoster(startDate);
        return ResponseEntity.ok(ApiResponse.success(roster, "AI tự động tối ưu và lưu bảng phân ca tuần thành công"));
    }

    @GetMapping("/weekly-registrations")
    public ResponseEntity<ApiResponse<List<com.phungvanlong.booking_hotel.dto.response.WeeklyShiftRegistrationResponse>>> getWeeklyRegistrations() {
        List<com.phungvanlong.booking_hotel.dto.response.WeeklyShiftRegistrationResponse> list = staffService.getPendingWeeklyRegistrations();
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh sách đơn đăng ký ca tuần chờ duyệt thành công"));
    }

    @PostMapping("/weekly-registrations/action")
    public ResponseEntity<ApiResponse<com.phungvanlong.booking_hotel.dto.response.WeeklyShiftRegistrationResponse>> processWeeklyRegistration(
            @Valid @RequestBody com.phungvanlong.booking_hotel.dto.request.ApproveWeeklyRegistrationRequest request,
            Authentication authentication) {
        String approverEmail = authentication != null ? authentication.getName() : null;
        com.phungvanlong.booking_hotel.dto.response.WeeklyShiftRegistrationResponse response = staffService.processWeeklyRegistration(request, approverEmail);
        return ResponseEntity.ok(ApiResponse.success(response, "Xử lý đơn đăng ký ca tuần thành công"));
    }
}
