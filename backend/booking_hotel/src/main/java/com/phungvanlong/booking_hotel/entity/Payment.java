package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", length = 100)
    private String transactionId; // Mã giao dịch trả về từ VNPay / Stripe

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // MOMO, VNPAY, STRIPE, CASH, POS_TERMINAL

    @Column(name = "ledger_type", length = 50)
    @Builder.Default
    private String ledgerType = "ROOM_CHARGE"; // ROOM_CHARGE, EXTRA_SERVICE, DEPOSIT, REFUND, OPEX

    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "reconciliation_note", columnDefinition = "TEXT")
    private String reconciliationNote;

    @Column(name = "reconciliation_time")
    private LocalDateTime reconciliationTime;

    @Column(name = "reconciled_by")
    private String reconciledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
}