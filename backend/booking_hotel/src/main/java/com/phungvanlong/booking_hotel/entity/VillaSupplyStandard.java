package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "villa_supply_standards", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"villa_id", "item_id"})
}, indexes = {
    @Index(name = "idx_supply_std_villa", columnList = "villa_id"),
    @Index(name = "idx_supply_std_item", columnList = "item_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillaSupplyStandard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_id", nullable = false)
    private Villa villa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(name = "standard_quantity", nullable = false)
    private Integer standardQuantity; // Định mức chuẩn (ví dụ: 6)

    @Column(length = 255)
    private String note;
}
