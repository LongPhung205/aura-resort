package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneResponse {

    private Long id;
    private String name;
    private String matchKey;
    private String tag;
    private String icon;
    private String badgeClass;
    private String description;
    private String slug;
    private String bannerUrl;
    private String highlights;
    private Integer displayOrder;
    private Boolean isActive;
    private long villaCount;
    private LocalDateTime createdAt;

    public static ZoneResponse fromEntity(Zone entity, long villaCount) {
        if (entity == null) return null;
        return ZoneResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .matchKey(entity.getMatchKey())
                .tag(entity.getTag())
                .icon(entity.getIcon())
                .badgeClass(entity.getBadgeClass())
                .description(entity.getDescription())
                .slug(entity.getSlug())
                .bannerUrl(entity.getBannerUrl())
                .highlights(entity.getHighlights())
                .displayOrder(entity.getDisplayOrder())
                .isActive(entity.getIsActive())
                .villaCount(villaCount)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
