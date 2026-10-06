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
    private String guestPhone;
    private String guestEmail;
    private String roomName;
    private BigDecimal amount;
    private String paymentMethod;
    private String ledgerType;
    private String status;
    private LocalDateTime paymentTime;
    private String reconciliationNote;
    private LocalDateTime reconciliationTime;
    private String reconciledBy;

    public static LedgerItemResponse fromEntity(Payment payment) {
        String guestName = payment.getGuestName();
        String guestPhone = null;
        String guestEmail = null;
        String bookingCode = null;
        String roomName = null;

        try {
            if (payment.getBooking() != null) {
                var b = payment.getBooking();
                bookingCode = b.getBookingCode();
                if (guestName == null || guestName.isBlank()) {
                    if (b.getGuestName() != null && !b.getGuestName().isBlank()) {
                        guestName = b.getGuestName();
                    } else if (b.getUser() != null) {
                        guestName = b.getUser().getFullName();
                    }
                }
                guestPhone = b.getGuestPhone() != null ? b.getGuestPhone() : (b.getUser() != null ? b.getUser().getPhone() : null);
                guestEmail = b.getGuestEmail() != null ? b.getGuestEmail() : (b.getUser() != null ? b.getUser().getEmail() : null);

                if (b.getBookingDetails() != null && !b.getBookingDetails().isEmpty()) {
                    var detail = b.getBookingDetails().get(0);
                    if (detail.getVilla() != null) {
                        roomName = detail.getVilla().getVillaNumber();
                    } else if (detail.getRoom() != null) {
                        roomName = "Phòng " + detail.getRoom().getRoomNumber();
                    }
                }
            }
        } catch (Exception ignored) {
            // Gracefully keep basic payment info if lazy proxy cannot be initialized
        }
        if (guestName == null || guestName.isBlank()) {
            guestName = "Khách vãng lai";
        }

        String txnId = payment.getTransactionId();
        if (txnId == null || txnId.isBlank()) {
            txnId = payment.getReferenceNo() != null ? payment.getReferenceNo() : "TXN-" + String.format("%06d", payment.getId());
        }

        LocalDateTime payTime = payment.getPaymentTime();
        if (payTime == null) {
            payTime = payment.getCreatedAt();
        }
        if (payTime == null && payment.getBooking() != null) {
            payTime = payment.getBooking().getCreatedAt();
        }
        if (payTime == null) {
            payTime = LocalDateTime.now();
        }

        return LedgerItemResponse.builder()
                .id(payment.getId())
                .transactionId(txnId)
                .referenceNo(payment.getReferenceNo())
                .bookingCode(bookingCode)
                .guestName(guestName)
                .guestPhone(guestPhone)
                .guestEmail(guestEmail)
                .roomName(roomName)
                .amount(payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO)
                .paymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "CASH")
                .ledgerType(payment.getLedgerType() != null ? payment.getLedgerType() : "ROOM_CHARGE")
                .status(payment.getStatus() != null ? payment.getStatus().name() : "PENDING")
                .paymentTime(payTime)
                .reconciliationNote(payment.getReconciliationNote())
                .reconciliationTime(payment.getReconciliationTime())
                .reconciledBy(payment.getReconciledBy())
                .build();
    }
}
