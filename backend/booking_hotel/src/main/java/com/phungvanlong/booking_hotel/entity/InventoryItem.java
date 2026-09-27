package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_items", indexes = {
    @Index(name = "idx_inventory_code", columnList = "code"),
    @Index(name = "idx_inventory_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InventoryCategory category;

    @Column(length = 30)
    @Builder.Default
    private String unit = "Chiếc";

    @Column(name = "in_stock", nullable = false)
    @Builder.Default
    private Integer inStock = 0;

    @Column(name = "min_threshold", nullable = false)
    @Builder.Default
    private Integer minThreshold = 10;

    @Column(name = "unit_price", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(length = 150)
    private String supplier;

    @Column(length = 100)
    @Builder.Default
    private String location = "Kho Tổng A1";

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InventoryTransaction> transactions = new ArrayList<>();
}
