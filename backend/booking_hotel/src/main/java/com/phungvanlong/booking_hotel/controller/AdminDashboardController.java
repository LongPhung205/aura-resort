package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.DashboardStatsResponse;
import com.phungvanlong.booking_hotel.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats(
            @RequestParam(required = false, defaultValue = "all") String resortId,
            @RequestParam(required = false, defaultValue = "7d") String period) {
        DashboardStatsResponse stats = adminDashboardService.getDashboardStats(resortId, period);
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy dữ liệu thống kê tổng quan thành công"));
    }
}
