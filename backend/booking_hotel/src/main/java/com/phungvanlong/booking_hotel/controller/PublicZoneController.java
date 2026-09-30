package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ZoneDetailResponse;
import com.phungvanlong.booking_hotel.dto.response.ZoneResponse;
import com.phungvanlong.booking_hotel.service.AdminZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/zones", "/public/zones"})
@RequiredArgsConstructor
public class PublicZoneController {

    private final AdminZoneService zoneService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getActiveZones() {
        List<ZoneResponse> zones = zoneService.getActiveZones();
        return ResponseEntity.ok(ApiResponse.success(zones, "Lấy danh sách phân khu thành công"));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ZoneDetailResponse>> getZoneDetailBySlug(@PathVariable String slug) {
        ZoneDetailResponse detail = zoneService.getZoneDetailBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(detail, "Lấy chi tiết phân khu thành công"));
    }
}
