package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "combo_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboPackage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    /** PUBLISH | DRAFT */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PUBLISH";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "combo_package_services",
        joinColumns = @JoinColumn(name = "combo_package_id"),
        inverseJoinColumns = @JoinColumn(name = "extra_service_id")
    )
    @Builder.Default
    private java.util.List<ExtraService> extraServices = new java.util.ArrayList<>();
}
