package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // Ví dụ: Deluxe King Room, Standard Twin

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice; // Giá gốc 1 đêm

    @Column(name = "dynamic_price", precision = 12, scale = 2)
    private BigDecimal dynamicPrice; // Giá áp dụng Yield Management AI

    @Column(name = "is_dynamic_pricing_enabled")
    @Builder.Default
    private Boolean isDynamicPricingEnabled = true;

    @Column(nullable = false)
    private Integer capacity; // Sức chứa tối đa (số người)

    @Column(name = "adults")
    @Builder.Default
    private Integer adults = 2; // Số người lớn tiêu chuẩn

    @Column(name = "children")
    @Builder.Default
    private Integer children = 0; // Số trẻ nhỏ tiêu chuẩn

    @Column(name = "bed_type", length = 150)
    private String bedType; // Ví dụ: 3 Giường King & Queen • 8 Người

    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl; // Ảnh đại diện chính lấy từ Cloudinary / upload

    // 1 hạng phòng có nhiều phòng vật lý
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RoomImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
}