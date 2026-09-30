package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_tickets", indexes = {
    @Index(name = "idx_mt_code", columnList = "ticket_code"),
    @Index(name = "idx_mt_status", columnList = "status"),
    @Index(name = "idx_mt_villa", columnList = "villa_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceTicket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_code", unique = true, nullable = false, length = 50)
    private String ticketCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id", nullable = false)
    private Villa villa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false, length = 50)
    private String category; // AIR_CONDITIONER, PLUMBING, ELECTRICAL, DOOR_LOCK, FURNITURE, OTHER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MaintenancePriority priority = MaintenancePriority.MEDIUM;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.REPORTED;

    @Column(name = "reported_by", nullable = false, length = 100)
    private String reportedBy;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "technician_note", columnDefinition = "TEXT")
    private String technicianNote;
}
