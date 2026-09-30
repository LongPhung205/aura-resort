package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ExtraServiceRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ExtraServiceResponse;
import com.phungvanlong.booking_hotel.service.ExtraServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra-services")
@RequiredArgsConstructor
public class ExtraServiceController {

    private final ExtraServiceService extraServiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExtraServiceResponse>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.success(extraServiceService.getAllServices(), "Lấy danh sách dịch vụ thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExtraServiceResponse>> createService(@Valid @RequestBody ExtraServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(extraServiceService.createService(request), "Tạo dịch vụ thành công"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExtraServiceResponse>> updateService(
            @PathVariable Long id, @Valid @RequestBody ExtraServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(extraServiceService.updateService(id, request), "Cập nhật dịch vụ thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        extraServiceService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa dịch vụ thành công"));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExtraServiceResponse>> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(extraServiceService.toggleActive(id), "Cập nhật trạng thái dịch vụ thành công"));
    }
}
