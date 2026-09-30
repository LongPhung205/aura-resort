package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.RoomConsumptionResponse;
import com.phungvanlong.booking_hotel.service.HousekeepingInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/billing/consumptions")
@RequiredArgsConstructor
public class AdminBillingConsumptionController {

    private final HousekeepingInspectionService inspectionService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<RoomConsumptionResponse>>> getPendingConsumptions(
            @RequestParam Long bookingId) {
        List<RoomConsumptionResponse> list = inspectionService.getPendingConsumptionsByBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh sách các khoản tiêu thụ buồng phòng chờ duyệt"));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<RoomConsumptionResponse>> approveConsumption(
            @PathVariable Long id,
            Authentication authentication) {
        String receptionist = authentication != null ? authentication.getName() : "reception@auroresort.com";
        RoomConsumptionResponse response = inspectionService.approveConsumption(id, receptionist);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã duyệt và cộng vào hóa đơn thanh toán"));
    }

    @PostMapping("/{id}/waive")
    public ResponseEntity<ApiResponse<RoomConsumptionResponse>> waiveConsumption(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        String receptionist = authentication != null ? authentication.getName() : "reception@auroresort.com";
        RoomConsumptionResponse response = inspectionService.waiveConsumption(id, reason, receptionist);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã miễn giảm khoản phí"));
    }
}
