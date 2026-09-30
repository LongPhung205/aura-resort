package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ZoneRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ZoneResponse;
import com.phungvanlong.booking_hotel.service.AdminZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/zones")
@RequiredArgsConstructor
public class AdminZoneController {

    private final AdminZoneService adminZoneService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getAllZones() {
        List<ZoneResponse> zones = adminZoneService.getAllZones();
        return ResponseEntity.ok(ApiResponse.success(zones, "Lấy danh sách phân khu thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getZoneById(@PathVariable Long id) {
        ZoneResponse zone = adminZoneService.getZoneById(id);
        return ResponseEntity.ok(ApiResponse.success(zone, "Lấy thông tin phân khu thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ZoneResponse>> createZone(@Valid @RequestBody ZoneRequest request) {
        ZoneResponse created = adminZoneService.createZone(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Tạo phân khu mới thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> updateZone(
            @PathVariable Long id,
            @Valid @RequestBody ZoneRequest request) {
        ZoneResponse updated = adminZoneService.updateZone(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật phân khu thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteZone(@PathVariable Long id) {
        adminZoneService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa phân khu thành công"));
    }
}
