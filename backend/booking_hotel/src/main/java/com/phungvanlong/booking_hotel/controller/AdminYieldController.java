package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.YieldMatrixResponse;
import com.phungvanlong.booking_hotel.dto.response.YieldRuleResponse;
import com.phungvanlong.booking_hotel.entity.YieldRule;
import com.phungvanlong.booking_hotel.service.AdminYieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/yield")
@RequiredArgsConstructor
public class AdminYieldController {

    private final AdminYieldService yieldService;

    @GetMapping("/rate-matrix")
    public ResponseEntity<ApiResponse<YieldMatrixResponse>> getRateMatrix() {
        YieldMatrixResponse response = yieldService.getRateMatrix();
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy ma trận giá và công suất lấp đầy thành công"));
    }

    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<YieldRuleResponse>>> getRules() {
        List<YieldRuleResponse> rules = yieldService.getAllRules();
        return ResponseEntity.ok(ApiResponse.success(rules, "Lấy danh sách quy tắc định giá thành công"));
    }

    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<YieldRuleResponse>> createRule(@RequestBody YieldRule rule) {
        YieldRuleResponse created = yieldService.createRule(rule);
        return ResponseEntity.ok(ApiResponse.success(created, "Thêm quy tắc định giá mới thành công"));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<YieldMatrixResponse>> applyDynamicPricing(
            @RequestParam(defaultValue = "true") boolean enableAi) {
        YieldMatrixResponse response = yieldService.applyDynamicPricing(enableAi);
        return ResponseEntity.ok(ApiResponse.success(response, "Đã áp dụng định giá động Yield Management thành công"));
    }
}
