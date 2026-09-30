package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "villa_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillaType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // Ví dụ: Grand Oceanfront Pool Villa, Sunset Lagoon Serenity Villa

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "dynamic_price", precision = 12, scale = 2)
    private BigDecimal dynamicPrice;

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
    private String imageUrl;

    @OneToMany(mappedBy = "villaType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Villa> villas = new ArrayList<>();

    @OneToMany(mappedBy = "villaType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
}
