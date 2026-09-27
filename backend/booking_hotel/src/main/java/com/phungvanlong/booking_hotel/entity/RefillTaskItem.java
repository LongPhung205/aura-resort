package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refill_task_items", indexes = {
    @Index(name = "idx_refill_item_task", columnList = "refill_task_id"),
    @Index(name = "idx_refill_item_item", columnList = "item_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefillTaskItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refill_task_id", nullable = false)
    private RefillTask refillTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(name = "standard_quantity", nullable = false)
    private Integer standardQuantity; // Định mức chuẩn (ví dụ: 6)

    @Column(name = "actual_quantity", nullable = false)
    private Integer actualQuantity; // Thực tế còn lại sau checkout (ví dụ: 2)

    @Column(name = "refill_quantity", nullable = false)
    private Integer refillQuantity; // Số lượng cần bổ sung = max(0, standard - actual) (ví dụ: 4)

    @Column(name = "consumed_quantity")
    @Builder.Default
    private Integer consumedQuantity = 0; // Đã tiêu thụ

    @Column(name = "damaged_quantity")
    @Builder.Default
    private Integer damagedQuantity = 0; // Bị hỏng / vỡ

    @Column(name = "missing_quantity")
    @Builder.Default
    private Integer missingQuantity = 0; // Thất thoát / mang đi

    @Column(name = "is_fulfilled")
    @Builder.Default
    private Boolean isFulfilled = false;
}
