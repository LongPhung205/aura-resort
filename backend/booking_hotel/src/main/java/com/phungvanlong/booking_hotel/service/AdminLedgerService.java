package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.DayEndClosingRequest;
import com.phungvanlong.booking_hotel.dto.response.DayEndClosingResponse;
import com.phungvanlong.booking_hotel.dto.response.LedgerItemResponse;

import java.util.List;

public interface AdminLedgerService {
    List<LedgerItemResponse> getLedgerTransactions(String ledgerType, String method);
    LedgerItemResponse createTransaction(com.phungvanlong.booking_hotel.dto.request.LedgerItemRequest request);
    DayEndClosingResponse executeDayEndClosing(DayEndClosingRequest request, String userEmail);
    DayEndClosingResponse getLatestClosing();
    LedgerItemResponse reconcileTransaction(Long id, com.phungvanlong.booking_hotel.dto.request.ReconcileRequest request, String userEmail);
    com.phungvanlong.booking_hotel.dto.response.PaymentDashboardStatsResponse getDashboardStats();
}
