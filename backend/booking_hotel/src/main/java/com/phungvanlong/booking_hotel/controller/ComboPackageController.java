package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ComboPackageRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ComboPackageResponse;
import com.phungvanlong.booking_hotel.service.ComboPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/combo-packages")
@RequiredArgsConstructor
public class ComboPackageController {

    private final ComboPackageService comboPackageService;

    /** Public — trang chủ dùng, không cần token */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ComboPackageResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(comboPackageService.getActive(), "OK"));
    }

    /** Admin only */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ComboPackageResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(comboPackageService.getAll(), "OK"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ComboPackageResponse>> create(@RequestBody ComboPackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(comboPackageService.create(request), "Tạo gói combo thành công"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ComboPackageResponse>> update(@PathVariable Long id,
                                                                     @RequestBody ComboPackageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(comboPackageService.update(id, request), "Cập nhật thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        comboPackageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa gói combo thành công"));
    }
}
