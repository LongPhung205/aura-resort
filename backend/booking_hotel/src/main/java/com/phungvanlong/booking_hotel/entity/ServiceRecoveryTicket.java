package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_recovery_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRecoveryTicket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(name = "guest_name", nullable = false, length = 100)
    private String guestName;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "incident_category", length = 50)
    private String incidentCategory; // AMENITY, FOOD_AND_BEVERAGE, NOISE, SERVICE, CLEANLINESS

    @Column(name = "issue_summary", nullable = false, columnDefinition = "TEXT")
    private String issueSummary;

    @Column(name = "resolution_action", length = 100)
    private String resolutionAction; // VOUCHER_OFFERED, FRUIT_BASKET, ROOM_UPGRADE, GM_CALL, REFUND

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_manager_id")
    private User assignedManager;

    @Column(name = "sla_minutes")
    @Builder.Default
    private Integer slaMinutes = 3;

    @Column(name = "actual_resolution_minutes")
    private Integer actualResolutionMinutes;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "OPEN"; // OPEN, IN_PROGRESS, RESOLVED, ESCALATED

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
