package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_code", unique = true, nullable = false, length = 30)
    private String bookingCode; // Mã đơn (ví dụ: BK-20260915-ABCD)

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "expire_at")
    private LocalDateTime expireAt; // Thời hạn giữ phòng (now + 15 phút)

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Column(name = "guest_phone", length = 20)
    private String guestPhone;

    @Column(name = "guest_email", length = 100)
    private String guestEmail;

    @Column(name = "id_card_number", length = 30)
    private String idCardNumber; // CCCD / Passport

    @Column(name = "deposit_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Column(name = "folio_balance", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal folioBalance = BigDecimal.ZERO;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 1 Đơn đặt phòng có thể đặt 1 hoặc nhiều phòng cùng lúc
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingDetail> bookingDetails = new ArrayList<>();

    // 1 Đơn đặt phòng có thể gắn với các bản ghi thanh toán (tiền cọc, phụ thu, dịch vụ)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    public Payment getPayment() {
        return (payments != null && !payments.isEmpty()) ? payments.get(0) : null;
    }

    public void setPayment(Payment payment) {
        if (this.payments == null) {
            this.payments = new ArrayList<>();
        }
        if (payment != null) {
            payment.setBooking(this);
            if (this.payments.isEmpty()) {
                this.payments.add(payment);
            } else {
                this.payments.set(0, payment);
            }
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingExtraService> extraServices = new ArrayList<>();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Review review;
}