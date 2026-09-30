package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ServiceDispatchRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ServiceDispatchResponse;
import com.phungvanlong.booking_hotel.service.AdminServiceDispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/services/dispatches")
@RequiredArgsConstructor
public class AdminServiceDispatchController {

    private final AdminServiceDispatchService dispatchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceDispatchResponse>>> getDispatches(
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        List<ServiceDispatchResponse> dispatches = dispatchService.getAllDispatches(status);
        return ResponseEntity.ok(ApiResponse.success(dispatches, "Lấy danh sách điều phối dịch vụ thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceDispatchResponse>> createDispatch(
            @Valid @RequestBody ServiceDispatchRequest request) {
        ServiceDispatchResponse response = dispatchService.createDispatch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Khởi tạo lệnh điều phối VIP thành công"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ServiceDispatchResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ServiceDispatchResponse response = dispatchService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật tiến trình điều phối thành công"));
    }
}
