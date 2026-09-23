package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LedgerItemRequest {
    private String title;
    private String guestName;
    @NotNull(message = "Số tiền không được để trống")
    private BigDecimal amount;
    private String paymentMethod; // CASH, BANK_TRANSFER, POS_TERMINAL, MOMO, VNPAY
    private String ledgerType; // ROOM_CHARGE, EXTRA_SERVICE, DEPOSIT, REFUND, OPEX
    private String referenceNo;
    private String notes;
}
