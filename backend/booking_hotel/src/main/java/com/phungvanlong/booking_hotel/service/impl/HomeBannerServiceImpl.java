package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.HomeBannerRequest;
import com.phungvanlong.booking_hotel.dto.response.HomeBannerResponse;
import com.phungvanlong.booking_hotel.entity.HomeBanner;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.HomeBannerRepository;
import com.phungvanlong.booking_hotel.service.HomeBannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeBannerServiceImpl implements HomeBannerService {

    private final HomeBannerRepository bannerRepository;

    @Override
    @Transactional
    public List<HomeBannerResponse> getActiveBanners() {
        List<HomeBanner> banners = bannerRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc();
        if (banners.isEmpty()) {
            return getAllBanners();
        }
        return banners.stream()
                .map(HomeBannerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<HomeBannerResponse> getBannersByPlacement(String placement) {
        List<HomeBanner> banners = bannerRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc();
        
        List<HomeBanner> filteredBanners = banners.stream()
                .filter(b -> b.getPlacement() == null || b.getPlacement().trim().isEmpty() || placement.equalsIgnoreCase(b.getPlacement()))
                .collect(Collectors.toList());
                
        if (filteredBanners.isEmpty() && "HOME".equalsIgnoreCase(placement)) {
            return getActiveBanners();
        }
        
        return filteredBanners.stream()
                .map(HomeBannerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<HomeBannerResponse> getAllBanners() {
        List<HomeBanner> banners = bannerRepository.findAllByOrderByDisplayOrderAsc();
        if (banners.isEmpty()) {
            HomeBanner b1 = HomeBanner.builder()
                    .title("Nâng Tầm Kỳ Nghỉ Đỉnh Cao")
                    .subtitle("Hệ Thống Nghỉ Dưỡng Thượng Lưu AURA")
                    .description("Khám phá không gian biệt thự biển biệt lập, hồ bơi riêng và dịch vụ quản gia cao cấp mang đến trải nghiệm nghỉ dưỡng hoàn mỹ.")
                    .imageUrl("/assets/images/rooms/grand-oceanfront.jpg")
                    .mobileImageUrl("/assets/images/rooms/grand-oceanfront.jpg")
                    .ctaText("Khám Phá Biệt Thự")
                    .ctaLink("/villas")
                    .displayOrder(1)
                    .isActive(true)
                    .build();
            banners = List.of(bannerRepository.save(b1));
        }
        return banners.stream()
                .map(HomeBannerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HomeBannerResponse getBannerById(Long id) {
        HomeBanner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy banner ID: " + id));
        return HomeBannerResponse.fromEntity(banner);
    }

    @Override
    @Transactional
    public HomeBannerResponse createBanner(HomeBannerRequest request) {
        int order = request.getDisplayOrder() != null ? request.getDisplayOrder() : 1;
        if (request.getDisplayOrder() == null) {
            order = (int) bannerRepository.count() + 1;
        }

        String img = request.getImageUrl() != null && !request.getImageUrl().isBlank()
                ? request.getImageUrl().trim()
                : "/assets/images/rooms/grand-oceanfront.jpg";

        HomeBanner banner = HomeBanner.builder()
                .title(request.getTitle() != null ? request.getTitle().trim() : "Banner Trang Chủ")
                .subtitle(request.getSubtitle() != null ? request.getSubtitle().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .imageUrl(img)
                .mobileImageUrl(img)
                .ctaText(request.getCtaText() != null ? request.getCtaText().trim() : "Khám Phá Ngay")
                .ctaLink(request.getCtaLink() != null ? request.getCtaLink().trim() : "/villas")
                .placement(request.getPlacement() != null ? request.getPlacement().trim() : "HOME")
                .displayOrder(order)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .badgesJson(request.getBadgesJson())
                .build();

        HomeBanner saved = bannerRepository.save(banner);
        return HomeBannerResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public HomeBannerResponse updateBanner(Long id, HomeBannerRequest request) {
        HomeBanner banner = bannerRepository.findById(id)
                .orElseGet(() -> HomeBanner.builder().build());

        banner.setTitle(request.getTitle() != null ? request.getTitle().trim() : "Banner Trang Chủ");
        banner.setSubtitle(request.getSubtitle() != null ? request.getSubtitle().trim() : null);
        banner.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        String img = request.getImageUrl() != null && !request.getImageUrl().isBlank()
                ? request.getImageUrl().trim()
                : "/assets/images/rooms/grand-oceanfront.jpg";
        banner.setImageUrl(img);
        banner.setMobileImageUrl(img);

        if (request.getCtaText() != null) {
            banner.setCtaText(request.getCtaText().trim());
        }
        if (request.getCtaLink() != null) {
            banner.setCtaLink(request.getCtaLink().trim());
        }
        if (request.getPlacement() != null) {
            banner.setPlacement(request.getPlacement().trim());
        }
        if (request.getDisplayOrder() != null) {
            banner.setDisplayOrder(request.getDisplayOrder());
        } else if (banner.getDisplayOrder() == null) {
            banner.setDisplayOrder(1);
        }
        if (request.getIsActive() != null) {
            banner.setIsActive(request.getIsActive());
        } else if (banner.getIsActive() == null) {
            banner.setIsActive(true);
        }
        if (request.getBadgesJson() != null) {
            banner.setBadgesJson(request.getBadgesJson());
        }

        HomeBanner updated = bannerRepository.save(banner);
        return HomeBannerResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void toggleBannerStatus(Long id) {
        HomeBanner banner = bannerRepository.findById(id).orElse(null);
        if (banner != null) {
            banner.setIsActive(!Boolean.TRUE.equals(banner.getIsActive()));
            bannerRepository.save(banner);
        }
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        HomeBanner banner = bannerRepository.findById(id).orElse(null);
        if (banner != null) {
            bannerRepository.delete(banner);
        }
    }

    private List<HomeBannerResponse> getDefaultBanners() {
        return List.of(
                HomeBannerResponse.builder()
                        .id(1L)
                        .title("Nâng Tầm Kỳ Nghỉ Đỉnh Cao")
                        .subtitle("Hệ Thống 12 Điểm Đến Thượng Lưu AURA")
                        .description("Khám phá không gian biệt thự biển biệt lập, hồ bơi riêng và dịch vụ quản gia cao cấp mang đến trải nghiệm nghỉ dưỡng hoàn mỹ.")
                        .imageUrl("/assets/images/rooms/grand-oceanfront.jpg")
                        .mobileImageUrl("/assets/images/rooms/grand-oceanfront.jpg")
                        .ctaText("Khám Phá Biệt Thự")
                        .ctaLink("/villas")
                        .displayOrder(1)
                        .isActive(true)
                        .build(),
                HomeBannerResponse.builder()
                        .id(2L)
                        .title("Trải Nghiệm Biệt Thự Biển Sầm Sơn")
                        .subtitle("FLC Sầm Sơn Luxury Resort & Villas")
                        .description("Không gian sang trọng, đón trọn làn gió biển và ánh bình minh rạng rỡ ngay tại các phân khu Ngọc Trai, Sao Biển & San Hô.")
                        .imageUrl("/assets/images/rooms/villa-beachfront.jpg")
                        .mobileImageUrl("/assets/images/rooms/villa-beachfront.jpg")
                        .ctaText("Xem Phân Khu Ngọc Trai")
                        .ctaLink("/villas/zone/villa-ngoc-trai")
                        .displayOrder(2)
                        .isActive(true)
                        .build(),
                HomeBannerResponse.builder()
                        .id(3L)
                        .title("Aura Elite Club VIP")
                        .subtitle("Đặc Quyền Nghỉ Dưỡng Thượng Khách")
                        .description("Đặc quyền ưu đãi độc quyền lên tới 25% cùng các tiện ích golf, spa và ẩm thực 5 sao dành riêng cho hội viên cao cấp.")
                        .imageUrl("/assets/images/rooms/royal-penthouse.jpg")
                        .mobileImageUrl("/assets/images/rooms/royal-penthouse.jpg")
                        .ctaText("Nhận Ưu Đãi Ngay")
                        .ctaLink("/promotions")
                        .displayOrder(3)
                        .isActive(true)
                        .build()
        );
    }
}
