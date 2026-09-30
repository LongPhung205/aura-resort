package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "yield_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YieldRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "condition_type", nullable = false, length = 50)
    private String conditionType; // OCCUPANCY_THRESHOLD, HOLIDAY_PEAK, LAST_MINUTE, EARLY_BIRD

    @Column(name = "threshold_value")
    private Double thresholdValue; // Ví dụ: 85.0 (lấp đầy > 85%)

    @Column(name = "price_multiplier", nullable = false)
    private Double priceMultiplier; // Ví dụ: 1.20 (+20%)

    @Column(name = "target_villa_types", length = 100)
    @Builder.Default
    private String targetVillaTypes = "ALL";

    @Column(name = "target_room_types", length = 100)
    @Builder.Default
    private String targetRoomTypes = "ALL";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(length = 255)
    private String description;
}
