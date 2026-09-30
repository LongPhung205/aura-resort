package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "day_end_closings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayEndClosing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "closing_date", nullable = false, unique = true)
    private LocalDate closingDate;

    @Column(name = "total_revenue", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "room_revenue", nullable = false, precision = 14, scale = 2)
    private BigDecimal roomRevenue;

    @Column(name = "service_revenue", nullable = false, precision = 14, scale = 2)
    private BigDecimal serviceRevenue;

    @Column(name = "total_opex", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalOpex;

    @Column(name = "net_cash", nullable = false, precision = 14, scale = 2)
    private BigDecimal netCash;

    @Column(name = "occupancy_rate")
    private Double occupancyRate;

    @Column(precision = 12, scale = 2)
    private BigDecimal adr; // Average Daily Rate

    @Column(precision = 12, scale = 2)
    private BigDecimal revPar; // Revenue Per Available Room

    @Column(name = "total_bookings")
    private Integer totalBookings;

    @Column(name = "occupied_rooms")
    private Integer occupiedRooms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_user_id")
    private User closedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "LOCKED"; // LOCKED, DRAFT

    @Column(columnDefinition = "TEXT")
    private String notes;
}
