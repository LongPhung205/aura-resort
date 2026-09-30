package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "housekeeping_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousekeepingTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quản lý dọn dẹp theo Villa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id")
    private Villa villa;

    // Quản lý dọn dẹp theo phòng ngủ con (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housekeeper_id")
    private User housekeeper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "task_type", nullable = false, length = 30)
    @Builder.Default
    private String taskType = "CHECKOUT_DEEP"; // CHECKOUT_DEEP, DAILY, TURNDOWN

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING"; // PENDING, IN_PROGRESS, OZONE_RUNNING, WAITING_QC, INSPECTED, RE_CLEAN, COMPLETED

    @Column(length = 20)
    @Builder.Default
    private String priority = "NORMAL"; // NORMAL, RUSH

    @Column(name = "ozone_enabled")
    @Builder.Default
    private Boolean ozoneEnabled = false;

    @Column(name = "re_clean_reason", length = 500)
    private String reCleanReason;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ozone_started_at")
    private LocalDateTime ozoneStartedAt;

    @Column(name = "ozone_ended_at")
    private LocalDateTime ozoneEndedAt;

    @Column(name = "checklist_json", columnDefinition = "TEXT")
    private String checklistJson;

    @Column(name = "evidence_photo_url")
    private String evidencePhotoUrl;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cleaning_note", length = 500)
    private String cleaningNote;

    @Column(name = "supervisor_note", length = 500)
    private String supervisorNote;
}
