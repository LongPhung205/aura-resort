package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.response.YieldMatrixResponse;
import com.phungvanlong.booking_hotel.dto.response.YieldRuleResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.repository.*;
import com.phungvanlong.booking_hotel.service.AdminYieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminYieldServiceImpl implements AdminYieldService {

    private final YieldRuleRepository yieldRuleRepository;
    private final VillaTypeRepository villaTypeRepository;
    private final VillaRepository villaRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    @Override
    public YieldMatrixResponse getRateMatrix() {
        List<Villa> allVillas = villaRepository.findAll();
        long totalVillas = allVillas.size();
        long occupiedVillas = allVillas.stream()
                .filter(v -> v.getStatus() == VillaStatus.OCCUPIED)
                .count();

        if (totalVillas == 0) {
            totalVillas = roomRepository.count();
            occupiedVillas = roomRepository.findAll().stream()
                    .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                    .count();
        }

        double currentOccupancy = totalVillas > 0 ? ((double) occupiedVillas / totalVillas) * 100.0 : 0.0;
        double predictedOccupancy = totalVillas > 0 ? Math.min(100.0, currentOccupancy + 12.5) : 0.0;

        List<VillaType> villaTypes = villaTypeRepository.findAll();
        List<YieldMatrixResponse.VillaTypeYieldItem> items = new ArrayList<>();

        if (!villaTypes.isEmpty()) {
            for (VillaType vt : villaTypes) {
                BigDecimal base = vt.getBasePrice() != null ? vt.getBasePrice() : BigDecimal.ZERO;
                BigDecimal dynamic = vt.getDynamicPrice() != null ? vt.getDynamicPrice() : base;

                double diff = 0.0;
                if (base.compareTo(BigDecimal.ZERO) > 0) {
                    diff = dynamic.subtract(base).divide(base, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                }
                String demand = diff >= 20.0 ? "PEAK" : diff >= 10.0 ? "HIGH" : diff >= 0 ? "NORMAL" : "LOW";

                items.add(YieldMatrixResponse.VillaTypeYieldItem.builder()
                        .villaTypeId(vt.getId())
                        .villaTypeName(vt.getName())
                        .basePrice(base)
                        .dynamicPrice(dynamic)
                        .occupancyRate(Math.round(currentOccupancy * 10.0) / 10.0)
                        .suggestedAdjustmentPercent(Math.round(diff * 10.0) / 10.0)
                        .demandLevel(demand)
                        .build());
            }
        } else {
            // Fallback to room types if villa types empty
            List<RoomType> roomTypes = roomTypeRepository.findAll();
            for (RoomType rt : roomTypes) {
                BigDecimal base = rt.getBasePrice() != null ? rt.getBasePrice() : BigDecimal.ZERO;
                BigDecimal dynamic = rt.getDynamicPrice() != null ? rt.getDynamicPrice() : base;
                double diff = 0.0;
                if (base.compareTo(BigDecimal.ZERO) > 0) {
                    diff = dynamic.subtract(base).divide(base, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                }
                String demand = diff >= 20.0 ? "PEAK" : diff >= 10.0 ? "HIGH" : diff >= 0 ? "NORMAL" : "LOW";

                items.add(YieldMatrixResponse.VillaTypeYieldItem.builder()
                        .villaTypeId(rt.getId())
                        .villaTypeName(rt.getName())
                        .basePrice(base)
                        .dynamicPrice(dynamic)
                        .occupancyRate(Math.round(currentOccupancy * 10.0) / 10.0)
                        .suggestedAdjustmentPercent(Math.round(diff * 10.0) / 10.0)
                        .demandLevel(demand)
                        .build());
            }
        }

        return YieldMatrixResponse.builder()
                .currentOccupancy(Math.round(currentOccupancy * 10.0) / 10.0)
                .predictedWeekendOccupancy(Math.round(predictedOccupancy * 10.0) / 10.0)
                .isAiDynamicPricingActive(true)
                .yieldStrategy("AGGRESSIVE")
                .villaTypes(items)
                .build();
    }

    @Override
    public List<YieldRuleResponse> getAllRules() {
        return yieldRuleRepository.findAll().stream()
                .map(YieldRuleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public YieldRuleResponse createRule(YieldRule rule) {
        return YieldRuleResponse.fromEntity(yieldRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public YieldMatrixResponse applyDynamicPricing(boolean enableAi) {
        double currentOccupancy = 0.0;
        if (enableAi) {
            List<Villa> allVillas = villaRepository.findAll();
            long totalVillas = allVillas.size();
            long occupiedVillas = allVillas.stream()
                    .filter(v -> v.getStatus() == VillaStatus.OCCUPIED)
                    .count();

            if (totalVillas == 0) {
                totalVillas = roomRepository.count();
                occupiedVillas = roomRepository.findAll().stream()
                        .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                        .count();
            }
            currentOccupancy = totalVillas > 0 ? ((double) occupiedVillas / totalVillas) * 100.0 : 0.0;
        }

        double maxMultiplier = 1.0;
        if (enableAi) {
            List<YieldRule> activeRules = yieldRuleRepository.findAll().stream()
                    .filter(r -> Boolean.TRUE.equals(r.getIsActive()))
                    .collect(Collectors.toList());

            for (YieldRule rule : activeRules) {
                if ("OCCUPANCY_THRESHOLD".equals(rule.getConditionType())) {
                    if (rule.getThresholdValue() != null && currentOccupancy >= rule.getThresholdValue()) {
                        if (rule.getPriceMultiplier() != null && rule.getPriceMultiplier() > maxMultiplier) {
                            maxMultiplier = rule.getPriceMultiplier();
                        }
                    }
                } else {
                    if (rule.getPriceMultiplier() != null && rule.getPriceMultiplier() > maxMultiplier) {
                        maxMultiplier = rule.getPriceMultiplier();
                    }
                }
            }
        }

        List<VillaType> villaTypes = villaTypeRepository.findAll();
        for (VillaType vt : villaTypes) {
            vt.setIsDynamicPricingEnabled(enableAi);
            if (enableAi) {
                BigDecimal base = vt.getBasePrice() != null ? vt.getBasePrice() : BigDecimal.valueOf(5000000);
                vt.setDynamicPrice(base.multiply(BigDecimal.valueOf(maxMultiplier)).setScale(0, RoundingMode.HALF_UP));
            } else {
                vt.setDynamicPrice(vt.getBasePrice());
            }
            villaTypeRepository.save(vt);
        }

        List<RoomType> roomTypes = roomTypeRepository.findAll();
        for (RoomType rt : roomTypes) {
            rt.setIsDynamicPricingEnabled(enableAi);
            if (enableAi) {
                BigDecimal base = rt.getBasePrice() != null ? rt.getBasePrice() : BigDecimal.valueOf(5000000);
                rt.setDynamicPrice(base.multiply(BigDecimal.valueOf(maxMultiplier)).setScale(0, RoundingMode.HALF_UP));
            } else {
                rt.setDynamicPrice(rt.getBasePrice());
            }
            roomTypeRepository.save(rt);
        }
        return getRateMatrix();
    }
}
