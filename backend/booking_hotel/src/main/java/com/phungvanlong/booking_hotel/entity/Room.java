package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"room_number", "villa_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, length = 30)
    private String roomNumber; // Ví dụ: "R101", "MB-01" (Master Bedroom)

    @Column(length = 100)
    private String name; // Ví dụ: "Master Bedroom", "Phòng ngủ 2 View Biển"

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả tiện nghi phòng con

    private Integer floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @Column(name = "ozone_status", length = 30)
    @Builder.Default
    private String ozoneStatus = "EXPIRED";

    @Column(name = "last_cleaned_at")
    private java.time.LocalDateTime lastCleanedAt;

    @Column(name = "current_guest_name", length = 100)
    private String currentGuestName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoomStatus status = RoomStatus.AVAILABLE;

    // Phòng con thuộc về 1 Villa cụ thể
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id")
    private Villa villa;

    // Hạng phòng con (Master, Deluxe, Twin,...) - optional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;
}