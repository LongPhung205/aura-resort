package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.*;
import com.phungvanlong.booking_hotel.dto.response.*;
import com.phungvanlong.booking_hotel.service.AdminHousekeepingService;
import com.phungvanlong.booking_hotel.service.HousekeepingInspectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/housekeeping")
@RequiredArgsConstructor
public class HousekeepingMobileController {

    private final AdminHousekeepingService housekeepingService;
    private final HousekeepingInspectionService inspectionService;

    private String getStaffEmail(Authentication authentication) {
        return authentication != null ? authentication.getName() : "hoa.housekeeping@auraholdings.vn";
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<ApiResponse<List<HousekeepingTaskResponse>>> getMyTasks(Authentication authentication) {
        String email = getStaffEmail(authentication);
        List<HousekeepingTaskResponse> tasks = housekeepingService.getMyTasks(email, null);
        return ResponseEntity.ok(ApiResponse.success(tasks, "Lấy danh sách phòng được giao thành công"));
    }

    @GetMapping("/available-dirty-rooms")
    public ResponseEntity<ApiResponse<List<HousekeepingTaskResponse>>> getAvailableDirtyRooms(Authentication authentication) {
        String email = getStaffEmail(authentication);
        List<HousekeepingTaskResponse> rooms = housekeepingService.getAvailableDirtyRooms(email);
        return ResponseEntity.ok(ApiResponse.success(rooms, "Lấy danh sách phòng trống cần dọn thành công"));
    }

    @PostMapping("/tasks/{id}/claim")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> claimTask(
            @PathVariable Long id,
            Authentication authentication) {
        String email = getStaffEmail(authentication);
        HousekeepingTaskResponse response = housekeepingService.claimTask(id, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã nhận dọn phòng thành công"));
    }

    @PostMapping("/tasks/{id}/start")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> startCleaning(
            @PathVariable Long id,
            Authentication authentication) {
        String email = getStaffEmail(authentication);
        HousekeepingTaskResponse response = housekeepingService.startCleaning(id, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Bắt đầu làm phòng"));
    }

    @PostMapping("/tasks/progress")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> updateProgress(
            @Valid @RequestBody UpdateCleaningProgressRequest request,
            Authentication authentication) {
        String email = getStaffEmail(authentication);
        HousekeepingTaskResponse response = housekeepingService.updateProgress(request, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật tiến độ thành công"));
    }

    @PostMapping("/tasks/{id}/toggle-ozone")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> toggleOzone(
            @PathVariable Long id,
            @RequestParam Boolean enabled) {
        HousekeepingTaskResponse response = housekeepingService.toggleOzone(id, enabled);
        return ResponseEntity.ok(ApiResponse.success(response, enabled ? "Đã bật chế độ khử khuẩn Ozone" : "Đã tắt chế độ Ozone"));
    }

    @PostMapping("/tasks/{id}/submit-inspection")
    public ResponseEntity<ApiResponse<List<RoomConsumptionResponse>>> submitInspection(
            @PathVariable Long id,
            @RequestBody SubmitRoomInspectionRequest request,
            Authentication authentication) {
        String email = getStaffEmail(authentication);
        List<RoomConsumptionResponse> consumptions = inspectionService.submitInspection(id, request, email);
        return ResponseEntity.ok(ApiResponse.success(consumptions, "Ghi nhận kiểm kê 3 nhóm thành công. Đã tự động tạo yêu cầu xuất bù kho!"));
    }

    @PostMapping("/tasks/{id}/submit-qc")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> submitQc(
            @PathVariable Long id,
            @RequestParam(required = false) String note,
            Authentication authentication) {
        String email = getStaffEmail(authentication);
        HousekeepingTaskResponse response = housekeepingService.submitQc(id, note, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Hoàn tất làm phòng. Đã gửi Giám sát nghiệm thu (WAITING_QC)!"));
    }

    @PostMapping("/lost-found")
    public ResponseEntity<ApiResponse<LostAndFoundResponse>> submitLostFound(
            @Valid @RequestBody LostAndFoundRequest request,
            Authentication authentication) {
        String email = getStaffEmail(authentication);
        LostAndFoundResponse response = inspectionService.createLostAndFound(request, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã ghi nhận đồ thất lạc vào sổ!"));
    }

    @PostMapping("/maintenance-tickets")
    public ResponseEntity<ApiResponse<MaintenanceTicketResponse>> submitMaintenance(
            @Valid @RequestBody MaintenanceTicketRequest request,
            Authentication authentication) {
        String email = getStaffEmail(authentication);
        MaintenanceTicketResponse response = inspectionService.createMaintenanceTicket(request, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã gửi báo cáo sự cố kỹ thuật thành công!"));
    }
}
