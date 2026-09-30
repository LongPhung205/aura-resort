package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.PromotionRequest;
import com.phungvanlong.booking_hotel.dto.response.PromotionResponse;
import com.phungvanlong.booking_hotel.entity.Promotion;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.PromotionRepository;
import com.phungvanlong.booking_hotel.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("Ngày bắt đầu không thể sau ngày kết thúc");
        }

        if (promotionRepository.findByCode(request.getCode()).isPresent()) {
            throw new BusinessException("Mã khuyến mãi đã tồn tại");
        }

        Promotion promotion = Promotion.builder()
                .code(request.getCode().toUpperCase())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .quantity(request.getQuantity())
                .name(request.getName())
                .category(request.getCategory())
                .build();

        Promotion saved = promotionRepository.save(promotion);
        return PromotionResponse.fromEntity(saved);
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy mã khuyến mãi"));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("Ngày bắt đầu không thể sau ngày kết thúc");
        }

        promotion.setName(request.getName());
        promotion.setCategory(request.getCategory());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setQuantity(request.getQuantity());

        return PromotionResponse.fromEntity(promotionRepository.save(promotion));
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy mã khuyến mãi"));
        promotionRepository.delete(promotion);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(PromotionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Promotion validatePromotionCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException("Mã khuyến mãi không hợp lệ"));

        LocalDate today = LocalDate.now();
        if (today.isBefore(promotion.getStartDate()) || today.isAfter(promotion.getEndDate())) {
            throw new BusinessException("Mã khuyến mãi đã hết hạn hoặc chưa đến ngày áp dụng");
        }

        if (promotion.getQuantity() != null && promotion.getQuantity() <= 0) {
            throw new BusinessException("Mã khuyến mãi đã hết lượt sử dụng");
        }

        return promotion;
    }
}
