package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.RoomImage;
import com.phungvanlong.booking_hotel.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer capacity;
    private Integer adults;
    private Integer children;
    private String bedType;
    private String imageUrl;
    private List<String> images;
    private Double averageRating;
    private Integer totalReviews;

    public static RoomTypeResponse fromEntity(RoomType entity) {
        int ad = entity.getAdults() != null ? entity.getAdults() : (entity.getCapacity() != null ? entity.getCapacity() : 2);
        int ch = entity.getChildren() != null ? entity.getChildren() : 0;
        int cap = entity.getCapacity() != null ? entity.getCapacity() : (ad + ch);

        List<String> imgUrls = null;
        try {
            if (entity.getImages() != null && !entity.getImages().isEmpty()) {
                imgUrls = entity.getImages().stream().map(RoomImage::getImageUrl).collect(Collectors.toList());
            }
        } catch (Exception ignored) {
            // Safe fallback if images collection is uninitialized outside transaction
        }

        return RoomTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice())
                .capacity(cap)
                .adults(ad)
                .children(ch)
                .bedType(entity.getBedType())
                .imageUrl(entity.getImageUrl())
                .images(imgUrls)
                .build();
    }
}
