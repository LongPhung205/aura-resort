package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.PromotionRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.PromotionResponse;
import com.phungvanlong.booking_hotel.entity.Promotion;
import com.phungvanlong.booking_hotel.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(@Valid @RequestBody PromotionRequest request) {
        PromotionResponse promotion = promotionService.createPromotion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promotion, "Tạo mã khuyến mãi thành công"));
    }

    // ✅ Public endpoint — no auth required — used by homepage
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getActivePromotions() {
        List<PromotionResponse> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, "Lấy danh sách khuyến mãi đang hoạt động thành công"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getAllPromotions() {
        List<PromotionResponse> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, "Lấy danh sách khuyến mãi thành công"));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<PromotionResponse>> checkPromotion(@RequestParam String code) {
        Promotion promotion = promotionService.validatePromotionCode(code);
        return ResponseEntity.ok(ApiResponse.success(PromotionResponse.fromEntity(promotion), "Mã khuyến mãi hợp lệ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponse>> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequest request) {
        PromotionResponse promotion = promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Cập nhật khuyến mãi thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa khuyến mãi thành công"));
    }
}
