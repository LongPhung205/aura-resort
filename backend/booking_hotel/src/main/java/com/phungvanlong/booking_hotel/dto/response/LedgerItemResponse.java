package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerItemResponse {
    private Long id;
    private String transactionId;
    private String referenceNo;
    private String bookingCode;
    private String guestName;
    private BigDecimal amount;
    private String paymentMethod;
    private String ledgerType;
    private String status;
    private LocalDateTime paymentTime;

    public static LedgerItemResponse fromEntity(Payment payment) {
        return LedgerItemResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .referenceNo(payment.getReferenceNo())
                .bookingCode(payment.getBooking() != null ? payment.getBooking().getBookingCode() : null)
                .guestName(payment.getGuestName() != null ? payment.getGuestName() : (payment.getBooking() != null && payment.getBooking().getUser() != null ? payment.getBooking().getUser().getFullName() : "Khách vãng lai"))
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .ledgerType(payment.getLedgerType())
                .status(payment.getStatus() != null ? payment.getStatus().name() : "PENDING")
                .paymentTime(payment.getPaymentTime() != null ? payment.getPaymentTime() : payment.getCreatedAt())
                .build();
    }
}
