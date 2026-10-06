package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "home_banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeBanner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", nullable = false, columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(name = "mobile_image_url", columnDefinition = "LONGTEXT")
    private String mobileImageUrl;

    @Column(name = "cta_text", length = 100)
    private String ctaText;

    @Column(name = "cta_link", length = 255)
    private String ctaLink;

    @Column(name = "placement", length = 50)
    @Builder.Default
    private String placement = "HOME"; // e.g. HOME, CATEGORY, AUTH

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "badges_json", columnDefinition = "TEXT")
    private String badgesJson;
}
