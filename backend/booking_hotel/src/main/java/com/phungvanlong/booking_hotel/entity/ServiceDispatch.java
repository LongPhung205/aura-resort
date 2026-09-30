package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_dispatches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDispatch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_extra_service_id")
    private BookingExtraService bookingExtraService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private User assignedStaff;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType; // MAYBACH_S680, YACHT_AURA_PEARL, SPA_IN_VILLA, BEACH_BBQ

    @Column(name = "asset_code", length = 50)
    private String assetCode; // Biển số xe hoặc mã tàu (51H-999.88, AURA-01)

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "pickup_location", length = 255)
    private String pickupLocation;

    @Column(name = "destination", length = 255)
    private String destination;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "flight_number", length = 30)
    private String flightNumber;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, DISPATCHED, IN_TRANSIT, COMPLETED, CANCELLED

    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
