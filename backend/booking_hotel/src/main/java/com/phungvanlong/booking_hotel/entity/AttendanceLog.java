package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    @Column(name = "check_time", nullable = false)
    private LocalDateTime checkTime;

    @Column(name = "check_type", nullable = false, length = 20)
    private String checkType; // CHECK_IN, CHECK_OUT

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String method = "FACE_ID"; // FACE_ID, GPS, RFID

    @Column(name = "location_name", length = 100)
    private String locationName; // Cổng chính, Sảnh Lễ tân, Bến du thuyền

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "match_accuracy")
    private Double matchAccuracy; // Độ khớp khuôn mặt (ví dụ: 99.4%)

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "VALID"; // VALID, LATE, EARLY_LEAVE, OUT_OF_GEOFENCE
}
