package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.HomeBannerRequest;
import com.phungvanlong.booking_hotel.dto.response.HomeBannerResponse;

import java.util.List;

public interface HomeBannerService {

    List<HomeBannerResponse> getActiveBanners();
    List<HomeBannerResponse> getBannersByPlacement(String placement);

    List<HomeBannerResponse> getAllBanners();

    HomeBannerResponse getBannerById(Long id);

    HomeBannerResponse createBanner(HomeBannerRequest request);

    HomeBannerResponse updateBanner(Long id, HomeBannerRequest request);

    void toggleBannerStatus(Long id);

    void deleteBanner(Long id);
}
