package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.AssignHousekeepingTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.HousekeepingChecklistRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateCleaningProgressRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.HousekeeperSummaryDto;
import com.phungvanlong.booking_hotel.dto.response.HousekeepingTaskResponse;
import com.phungvanlong.booking_hotel.service.AdminHousekeepingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/housekeeping")
@RequiredArgsConstructor
public class AdminHousekeepingController {

    private final AdminHousekeepingService housekeepingService;
    private final com.phungvanlong.booking_hotel.service.HousekeepingInspectionService inspectionService;

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<HousekeepingTaskResponse>>> getTasks(
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        List<HousekeepingTaskResponse> tasks = housekeepingService.getAllTasks(status);
        return ResponseEntity.ok(ApiResponse.success(tasks, "Lấy danh sách nhiệm vụ buồng phòng thành công"));
    }

    @GetMapping("/housekeepers")
    public ResponseEntity<ApiResponse<List<HousekeeperSummaryDto>>> getHousekeepers() {
        List<HousekeeperSummaryDto> housekeepers = housekeepingService.getAvailableHousekeepers();
        return ResponseEntity.ok(ApiResponse.success(housekeepers, "Lấy danh sách nhân viên buồng phòng thành công"));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<ApiResponse<List<HousekeepingTaskResponse>>> getMyTasks(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long housekeeperId,
            Authentication authentication) {
        String staffEmail = email;
        if ((staffEmail == null || staffEmail.isBlank()) && authentication != null) {
            staffEmail = authentication.getName();
        }
        List<HousekeepingTaskResponse> tasks = housekeepingService.getMyTasks(staffEmail, housekeeperId);
        return ResponseEntity.ok(ApiResponse.success(tasks, "Lấy danh sách phòng được giao thành công"));
    }

    @PostMapping("/tasks/assign")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> assignTask(
            @Valid @RequestBody AssignHousekeepingTaskRequest request,
            Authentication authentication) {
        String supervisorEmail = authentication != null ? authentication.getName() : null;
        HousekeepingTaskResponse response = housekeepingService.assignTask(request, supervisorEmail);
        return ResponseEntity.ok(ApiResponse.success(response, "Phân công dọn phòng thành công"));
    }

    @PostMapping("/tasks/{id}/start-cleaning")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> startCleaning(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        HousekeepingTaskResponse response = housekeepingService.startCleaning(id, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã bắt đầu tiến trình dọn dẹp phòng"));
    }

    // Alias for backward compatibility
    @PostMapping("/tasks/{id}/start-ozone")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> startOzone(@PathVariable Long id) {
        return startCleaning(id, null);
    }

    @PostMapping("/tasks/progress")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> updateProgress(
            @Valid @RequestBody UpdateCleaningProgressRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        HousekeepingTaskResponse response = housekeepingService.updateProgress(request, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật tiến độ dọn dẹp thành công"));
    }

    @PostMapping("/tasks/{id}/complete")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> completeCleaning(
            @PathVariable Long id,
            @RequestParam(required = false) String note,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        HousekeepingTaskResponse response = housekeepingService.completeCleaning(id, note, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Báo cáo hoàn thành dọn phòng thành công. Chờ quản lý nghiệm thu!"));
    }

    @PostMapping("/tasks/checklist")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> submitChecklist(
            @Valid @RequestBody HousekeepingChecklistRequest request) {
        HousekeepingTaskResponse response = housekeepingService.submitChecklist(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Gửi biên bản nghiệm thu 16 bước thành công"));
    }

    @PostMapping("/tasks/{id}/approve")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> approveTask(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        HousekeepingTaskResponse response = housekeepingService.approveTask(id, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Nghiệm thu buồng phòng thành công. Phòng đã sẵn sàng đón khách!"));
    }

    @PostMapping("/tasks/{id}/reject")
    public ResponseEntity<ApiResponse<HousekeepingTaskResponse>> rejectTask(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        HousekeepingTaskResponse response = housekeepingService.rejectTask(id, reason, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã yêu cầu nhân viên dọn lại phòng"));
    }

    @GetMapping("/lost-found")
    public ResponseEntity<ApiResponse<List<com.phungvanlong.booking_hotel.dto.response.LostAndFoundResponse>>> getLostFound(
            @RequestParam(required = false) com.phungvanlong.booking_hotel.entity.LostAndFoundStatus status,
            @RequestParam(required = false) Long villaId) {
        List<com.phungvanlong.booking_hotel.dto.response.LostAndFoundResponse> list = inspectionService.getLostAndFoundList(status, villaId);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh sách đồ thất lạc thành công"));
    }

    @PutMapping("/lost-found/{id}/status")
    public ResponseEntity<ApiResponse<com.phungvanlong.booking_hotel.dto.response.LostAndFoundResponse>> updateLostFoundStatus(
            @PathVariable Long id,
            @RequestParam com.phungvanlong.booking_hotel.entity.LostAndFoundStatus status,
            @RequestParam(required = false) String note) {
        com.phungvanlong.booking_hotel.dto.response.LostAndFoundResponse response = inspectionService.updateLostAndFoundStatus(id, status, note);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái đồ thất lạc thành công"));
    }

    @DeleteMapping("/clear-all-data")
    public ResponseEntity<ApiResponse<String>> clearAllData() {
        housekeepingService.clearAllData();
        return ResponseEntity.ok(ApiResponse.success("Đã xóa hết", "Đã xóa toàn bộ dữ liệu dọn phòng"));
    }

    @GetMapping("/maintenance-tickets")
    public ResponseEntity<ApiResponse<List<com.phungvanlong.booking_hotel.dto.response.MaintenanceTicketResponse>>> getMaintenanceTickets(
            @RequestParam(required = false) com.phungvanlong.booking_hotel.entity.MaintenanceStatus status,
            @RequestParam(required = false) Long villaId) {
        List<com.phungvanlong.booking_hotel.dto.response.MaintenanceTicketResponse> list = inspectionService.getMaintenanceTickets(status, villaId);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh sách phiếu bảo trì phòng thành công"));
    }

    @PutMapping("/maintenance-tickets/{id}/status")
    public ResponseEntity<ApiResponse<com.phungvanlong.booking_hotel.dto.response.MaintenanceTicketResponse>> updateMaintenanceStatus(
            @PathVariable Long id,
            @RequestParam com.phungvanlong.booking_hotel.entity.MaintenanceStatus status,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) String technicianName) {
        com.phungvanlong.booking_hotel.dto.response.MaintenanceTicketResponse response = inspectionService.updateMaintenanceTicketStatus(id, status, note, technicianName);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái phiếu bảo trì thành công"));
    }
}
