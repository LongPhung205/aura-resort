package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Collection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectionResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isActive;

    public static CollectionResponse fromEntity(Collection collection) {
        if (collection == null) return null;
        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .slug(collection.getSlug())
                .description(collection.getDescription())
                .imageUrl(collection.getImageUrl())
                .displayOrder(collection.getDisplayOrder())
                .isActive(collection.getIsActive())
                .build();
    }
}
