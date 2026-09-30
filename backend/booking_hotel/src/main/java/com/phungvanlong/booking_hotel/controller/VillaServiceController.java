package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.VillaServiceRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaServiceResponse;
import com.phungvanlong.booking_hotel.service.VillaServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/villa-services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class VillaServiceController {

    private final VillaServiceService villaServiceService;

    /** Lấy danh sách dịch vụ đã gán cho 1 Villa */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VillaServiceResponse>>> getByVilla(@RequestParam Long villaId) {
        return ResponseEntity.ok(ApiResponse.success(
                villaServiceService.getByVillaId(villaId),
                "Lấy danh sách dịch vụ Villa thành công"));
    }

    /** Xem Villa nào đang dùng dịch vụ X */
    @GetMapping("/by-service/{serviceId}")
    public ResponseEntity<ApiResponse<List<VillaServiceResponse>>> getByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(ApiResponse.success(
                villaServiceService.getByServiceId(serviceId),
                "Lấy danh sách Villa theo dịch vụ thành công"));
    }

    /** Gán 1 dịch vụ cho 1 Villa */
    @PostMapping
    public ResponseEntity<ApiResponse<VillaServiceResponse>> assign(@Valid @RequestBody VillaServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(villaServiceService.assign(request), "Gán dịch vụ cho Villa thành công"));
    }

    /** Gán nhiều dịch vụ cho 1 Villa cùng lúc */
    @PostMapping("/bulk/{villaId}")
    public ResponseEntity<ApiResponse<List<VillaServiceResponse>>> bulkAssign(
            @PathVariable Long villaId,
            @RequestBody List<VillaServiceRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(villaServiceService.bulkAssign(villaId, requests), "Gán hàng loạt dịch vụ thành công"));
    }

    /** Cập nhật cấu hình (giá riêng, ghi chú) */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VillaServiceResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody VillaServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(villaServiceService.update(id, request), "Cập nhật cấu hình dịch vụ Villa thành công"));
    }

    /** Gỡ dịch vụ khỏi Villa */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long id) {
        villaServiceService.remove(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Gỡ dịch vụ khỏi Villa thành công"));
    }

    /** Bật/tắt isAvailable */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<VillaServiceResponse>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(villaServiceService.toggleAvailable(id), "Cập nhật trạng thái dịch vụ Villa thành công"));
    }
}
