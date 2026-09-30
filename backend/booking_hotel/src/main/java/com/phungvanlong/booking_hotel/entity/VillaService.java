package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "villa_services",
       uniqueConstraints = @UniqueConstraint(columnNames = {"villa_id", "service_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillaService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id", nullable = false)
    private Villa villa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ExtraService service;

    /** Giá riêng của Villa cho dịch vụ này. NULL = dùng giá mặc định từ ExtraService.price */
    @Column(name = "price_override", precision = 12, scale = 2)
    private BigDecimal priceOverride;

    /** Villa có đang cung cấp dịch vụ này không */
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(columnDefinition = "TEXT")
    private String note;
}
