package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.HomeBannerResponse;
import com.phungvanlong.booking_hotel.service.HomeBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/banners", "/public/banners"})
@RequiredArgsConstructor
public class PublicBannerController {

    private final HomeBannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HomeBannerResponse>>> getActiveBanners(
            @RequestParam(required = false) String placement) {
        List<HomeBannerResponse> banners;
        if (placement != null && !placement.isBlank()) {
            banners = bannerService.getBannersByPlacement(placement.trim());
        } else {
            banners = bannerService.getActiveBanners();
        }
        return ResponseEntity.ok(ApiResponse.success(banners, "Lấy danh sách banner thành công"));
    }
}
