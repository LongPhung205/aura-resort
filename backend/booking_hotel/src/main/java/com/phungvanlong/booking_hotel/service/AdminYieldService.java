package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.response.YieldMatrixResponse;
import com.phungvanlong.booking_hotel.dto.response.YieldRuleResponse;
import com.phungvanlong.booking_hotel.entity.YieldRule;

import java.util.List;

public interface AdminYieldService {
    YieldMatrixResponse getRateMatrix();
    List<YieldRuleResponse> getAllRules();
    YieldRuleResponse createRule(YieldRule rule);
    YieldMatrixResponse applyDynamicPricing(boolean enableAi);
}
