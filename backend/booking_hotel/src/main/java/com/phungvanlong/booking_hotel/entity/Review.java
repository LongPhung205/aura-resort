package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(length = 20)
    @Builder.Default
    private String sentiment = "POSITIVE"; // POSITIVE, NEUTRAL, NEGATIVE

    @Column(name = "management_reply", columnDefinition = "TEXT")
    private String managementReply;

    @Column(name = "replied_at")
    private java.time.LocalDateTime repliedAt;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true, nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_type_id")
    private VillaType villaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;
}
