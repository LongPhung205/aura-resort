package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "match_key", length = 100)
    private String matchKey;

    @Column(length = 50)
    private String tag;

    @Column(length = 50)
    private String icon;

    @Column(name = "badge_class", length = 100)
    private String badgeClass;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(unique = true, length = 120)
    private String slug;

    @Column(name = "banner_url", columnDefinition = "LONGTEXT")
    private String bannerUrl;

    @Column(columnDefinition = "TEXT")
    private String highlights;

    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 1;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
