package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.HomeBannerRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.HomeBannerResponse;
import com.phungvanlong.booking_hotel.service.HomeBannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final HomeBannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HomeBannerResponse>>> getAllBanners() {
        List<HomeBannerResponse> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(ApiResponse.success(banners, "Lấy danh sách tất cả banner thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeBannerResponse>> getBannerById(@PathVariable Long id) {
        HomeBannerResponse banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(ApiResponse.success(banner, "Lấy chi tiết banner thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HomeBannerResponse>> createBanner(@Valid @RequestBody HomeBannerRequest request) {
        HomeBannerResponse created = bannerService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Thêm mới banner thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeBannerResponse>> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody HomeBannerRequest request) {
        HomeBannerResponse updated = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật banner thành công"));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleBannerStatus(@PathVariable Long id) {
        bannerService.toggleBannerStatus(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Thay đổi trạng thái banner thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa banner thành công"));
    }
}
