package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "extra_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** Phân loại: DINING, TRANSPORT, SPA, ENTERTAINMENT, CLEANING, OTHER */
    @Column(length = 30)
    @Builder.Default
    private String type = "OTHER";

    /** Đơn vị tính: người, gói, ngày, lần, chuyến */
    @Column(length = 30)
    @Builder.Default
    private String unit = "lần";

    /** Material Symbols icon name, ví dụ: restaurant, two_wheeler */
    @Column(length = 50)
    private String icon;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VillaService> villaServices = new ArrayList<>();
}
