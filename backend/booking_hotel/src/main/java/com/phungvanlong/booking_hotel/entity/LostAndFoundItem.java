package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lost_and_found_items", indexes = {
    @Index(name = "idx_lf_code", columnList = "item_code"),
    @Index(name = "idx_lf_status", columnList = "status"),
    @Index(name = "idx_lf_villa", columnList = "villa_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostAndFoundItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", unique = true, nullable = false, length = 50)
    private String itemCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id", nullable = false)
    private Villa villa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(length = 50)
    @Builder.Default
    private String category = "OTHER"; // ELECTRONICS, JEWELRY, CLOTHING, DOCUMENTS, OTHER

    @Column(name = "found_location", nullable = false, length = 200)
    private String foundLocation;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "finder_name", nullable = false, length = 100)
    private String finderName;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Column(name = "guest_phone", length = 30)
    private String guestPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private LostAndFoundStatus status = LostAndFoundStatus.STORED;

    @Column(name = "storage_location", length = 100)
    @Builder.Default
    private String storageLocation = "Kho Buồng Phòng";

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(columnDefinition = "TEXT")
    private String note;
}
