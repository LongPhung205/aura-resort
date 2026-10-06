package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.DayEndClosingRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.DayEndClosingResponse;
import com.phungvanlong.booking_hotel.dto.response.LedgerItemResponse;
import com.phungvanlong.booking_hotel.dto.response.PaymentDashboardStatsResponse;
import com.phungvanlong.booking_hotel.dto.request.ReconcileRequest;
import com.phungvanlong.booking_hotel.service.AdminLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminLedgerController {

    private final AdminLedgerService ledgerService;

    @GetMapping("/ledger")
    public ResponseEntity<ApiResponse<List<LedgerItemResponse>>> getLedger(
            @RequestParam(required = false, defaultValue = "ALL") String ledgerType,
            @RequestParam(required = false, defaultValue = "ALL") String method) {
        List<LedgerItemResponse> items = ledgerService.getLedgerTransactions(ledgerType, method);
        return ResponseEntity.ok(ApiResponse.success(items, "Lấy sổ cái chi tiết PMS thành công"));
    }

    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<LedgerItemResponse>> createTransaction(
            @jakarta.validation.Valid @RequestBody com.phungvanlong.booking_hotel.dto.request.LedgerItemRequest request) {
        LedgerItemResponse response = ledgerService.createTransaction(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Lập phiếu thu/chi mới thành công"));
    }

    @PostMapping("/day-end-closing")
    public ResponseEntity<ApiResponse<DayEndClosingResponse>> executeClosing(
            @RequestBody(required = false) DayEndClosingRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        DayEndClosingResponse response = ledgerService.executeDayEndClosing(request, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Thực hiện chốt sổ ngày (Night Audit) thành công"));
    }

    @GetMapping("/latest-closing")
    public ResponseEntity<ApiResponse<DayEndClosingResponse>> getLatestClosing() {
        DayEndClosingResponse response = ledgerService.getLatestClosing();
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy dữ liệu chốt sổ gần nhất thành công"));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<PaymentDashboardStatsResponse>> getDashboardStats() {
        PaymentDashboardStatsResponse response = ledgerService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy dữ liệu thống kê tài chính thành công"));
    }

    @PutMapping("/transactions/{id}/reconcile")
    public ResponseEntity<ApiResponse<LedgerItemResponse>> reconcileTransaction(
            @PathVariable Long id,
            @RequestBody ReconcileRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        LedgerItemResponse response = ledgerService.reconcileTransaction(id, request, email);
        return ResponseEntity.ok(ApiResponse.success(response, "Đối soát giao dịch thành công"));
    }
}
