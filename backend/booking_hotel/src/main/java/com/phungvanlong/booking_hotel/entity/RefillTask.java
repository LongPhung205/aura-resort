package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "refill_tasks", indexes = {
    @Index(name = "idx_refill_task_code", columnList = "task_code"),
    @Index(name = "idx_refill_villa", columnList = "villa_id"),
    @Index(name = "idx_refill_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefillTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_code", unique = true, nullable = false, length = 50)
    private String taskCode; // VD: RF-20260928-01

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id", nullable = false)
    private Villa villa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housekeeping_task_id")
    private HousekeepingTask housekeepingTask;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RefillTaskStatus status = RefillTaskStatus.PENDING;

    @Column(length = 100)
    private String creator; // Người lập phiếu kiểm kê

    @Column(name = "assigned_staff", length = 100)
    private String assignedStaff; // Người nhận nhiệm vụ vận chuyển vật tư

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "refillTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RefillTaskItem> items = new ArrayList<>();
}
