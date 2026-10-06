package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.PromotionRequest;
import com.phungvanlong.booking_hotel.dto.response.PromotionResponse;
import com.phungvanlong.booking_hotel.entity.Promotion;

import java.util.List;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    void deletePromotion(Long id);
    List<PromotionResponse> getAllPromotions();
    List<PromotionResponse> getActivePromotions();
    Promotion validatePromotionCode(String code);
}
