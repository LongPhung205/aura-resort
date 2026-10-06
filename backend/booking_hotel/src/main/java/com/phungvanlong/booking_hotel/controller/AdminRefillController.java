package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.CreateRefillTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaSupplyStandardRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.RefillTaskResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaInventoryResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaSupplyStandardResponse;
import com.phungvanlong.booking_hotel.entity.RefillTaskStatus;
import com.phungvanlong.booking_hotel.service.AdminRefillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory/refill")
@RequiredArgsConstructor
public class AdminRefillController {

    private final AdminRefillService refillService;

    // --- 1. TIÊU CHUẨN ĐỊNH MỨC VẬT TƯ VILLA ---
    @GetMapping("/standards")
    public ResponseEntity<ApiResponse<List<VillaSupplyStandardResponse>>> getStandards(
            @RequestParam(required = false) Long villaId) {
        List<VillaSupplyStandardResponse> list = refillService.getStandardsByVilla(villaId);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy định mức tiêu chuẩn vật tư villa thành công"));
    }

    @PostMapping("/standards")
    public ResponseEntity<ApiResponse<VillaSupplyStandardResponse>> saveStandard(
            @Valid @RequestBody VillaSupplyStandardRequest request) {
        VillaSupplyStandardResponse response = refillService.saveStandard(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Lưu định mức tiêu chuẩn villa thành công"));
    }

    @DeleteMapping("/standards/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStandard(@PathVariable Long id) {
        refillService.deleteStandard(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa định mức thành công"));
    }

    @PostMapping("/standards/apply-defaults/{villaId}")
    public ResponseEntity<ApiResponse<Void>> applyDefaultStandards(@PathVariable Long villaId) {
        refillService.applyDefaultStandardsToVilla(villaId);
        return ResponseEntity.ok(ApiResponse.success(null, "Áp dụng định mức tiêu chuẩn mẫu cho villa thành công"));
    }

    @PostMapping("/standards/apply-defaults-all")
    public ResponseEntity<ApiResponse<Void>> applyDefaultStandardsToAllVillas() {
        refillService.applyDefaultStandardsToAllVillas();
        return ResponseEntity.ok(ApiResponse.success(null, "Áp dụng định mức tiêu chuẩn mẫu cho TẤT CẢ villa thành công"));
    }

    // --- 2. TỒN KHO THỰC TẾ TẠI VILLA ---
    @GetMapping("/villa-stock")
    public ResponseEntity<ApiResponse<List<VillaInventoryResponse>>> getVillaInventory(
            @RequestParam Long villaId) {
        List<VillaInventoryResponse> list = refillService.getVillaInventory(villaId);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh mục tồn kho thực tế tại villa thành công"));
    }

    // --- 3. NHIỆM VỤ REFILL / BỔ SUNG VẬT TƯ ---
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<RefillTaskResponse>>> getRefillTasks(
            @RequestParam(required = false) RefillTaskStatus status,
            @RequestParam(required = false) Long villaId) {
        List<RefillTaskResponse> list = refillService.getRefillTasks(status, villaId);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh sách nhiệm vụ refill villa thành công"));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse<RefillTaskResponse>> getRefillTaskById(@PathVariable Long id) {
        RefillTaskResponse response = refillService.getRefillTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết nhiệm vụ refill thành công"));
    }

    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<RefillTaskResponse>> createRefillTask(
            @Valid @RequestBody CreateRefillTaskRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        RefillTaskResponse response = refillService.createRefillTask(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo nhiệm vụ bổ sung vật tư (Refill) thành công"));
    }

    @PostMapping("/tasks/{id}/fulfill")
    public ResponseEntity<ApiResponse<RefillTaskResponse>> fulfillRefillTask(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        RefillTaskResponse response = refillService.fulfillRefillTask(id, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã xuất kho và hoàn tất bổ sung vật tư cho Villa!"));
    }
}
