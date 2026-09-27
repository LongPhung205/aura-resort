package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "villa_assets", indexes = {
    @Index(name = "idx_asset_villa", columnList = "villa_id"),
    @Index(name = "idx_asset_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillaAsset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id")
    private Villa villa;

    @Column(name = "villa_number", length = 50)
    private String villaNumber;

    @Column(name = "asset_name", nullable = false, length = 150)
    private String assetName;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(length = 80)
    private String category; // Điện tử, Vệ sinh, Gia dụng, Nội thất, Thiết bị thông minh

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VillaAssetStatus status = VillaAssetStatus.GOOD;

    @Column(name = "install_date")
    private LocalDate installDate;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @Column(columnDefinition = "TEXT")
    private String note;
}
