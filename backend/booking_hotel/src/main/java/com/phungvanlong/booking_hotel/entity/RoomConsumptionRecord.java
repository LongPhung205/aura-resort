package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_consumption_records", indexes = {
    @Index(name = "idx_rc_booking", columnList = "booking_id"),
    @Index(name = "idx_rc_task", columnList = "housekeeping_task_id"),
    @Index(name = "idx_rc_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomConsumptionRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housekeeping_task_id", nullable = false)
    private HousekeepingTask housekeepingTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id", nullable = false)
    private Villa villa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 30)
    private RoomConsumptionItemType itemType;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RoomConsumptionStatus status = RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL;

    @Column(name = "evidence_photo_url", length = 500)
    private String evidencePhotoUrl;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "recorded_by", length = 100)
    private String recordedBy;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;
}
