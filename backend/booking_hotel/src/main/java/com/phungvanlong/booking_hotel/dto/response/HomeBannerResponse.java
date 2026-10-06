package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.HomeBanner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeBannerResponse {

    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String mobileImageUrl;
    private String ctaText;
    private String ctaLink;
    private String placement;
    private Integer displayOrder;
    private Boolean isActive;
    private String badgesJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static HomeBannerResponse fromEntity(HomeBanner entity) {
        if (entity == null) return null;
        return HomeBannerResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .subtitle(entity.getSubtitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .mobileImageUrl(entity.getMobileImageUrl())
                .ctaText(entity.getCtaText())
                .ctaLink(entity.getCtaLink())
                .placement(entity.getPlacement())
                .displayOrder(entity.getDisplayOrder())
                .isActive(entity.getIsActive())
                .badgesJson(entity.getBadgesJson())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
