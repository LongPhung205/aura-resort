package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaImage;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaResponse implements Serializable {
    private Long id;
    private String villaNumber;
    private Integer floor;
    private String structureType;
    private BigDecimal basePrice;
    private VillaStatus status;
    private Long villaTypeId;
    private String villaTypeName;
    private String zone;
    private String ozoneStatus;
    private List<String> amenities;
    private LocalDateTime lastCleanedAt;
    private String currentGuestName;
    private Integer bedroomCount;
    private Integer totalBeds;
    private Integer totalAdults;
    private Integer totalChildren;
    private Integer totalCapacity;
    private String imageUrl;
    private List<String> images;
    private List<ChildRoomDto> rooms;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildRoomDto implements Serializable {
        private Long id;
        private String roomNumber;
        private String name;
        private String description;
        private Integer floor;
        private String status;
        private Long roomTypeId;
        private String roomTypeName;
        private String bedType;
        private Integer adults;
        private Integer children;
        private Integer capacity;
    }

    public static VillaResponse fromEntity(Villa entity) {
        if (entity == null) return null;

        List<ChildRoomDto> childRooms = new ArrayList<>();
        int calculatedAdults = 0;
        int calculatedChildren = 0;
        int calculatedCapacity = 0;

        if (entity.getRooms() != null) {
            childRooms = entity.getRooms().stream()
                    .map(r -> {
                        Long rtId = r.getRoomType() != null ? r.getRoomType().getId() : null;
                        String rtName = r.getRoomType() != null ? r.getRoomType().getName() : null;
                        String bType = r.getRoomType() != null ? r.getRoomType().getBedType() : null;
                        Integer adults = r.getRoomType() != null ? r.getRoomType().getAdults() : 2;
                        Integer children = r.getRoomType() != null ? r.getRoomType().getChildren() : 0;
                        Integer capacity = r.getRoomType() != null ? r.getRoomType().getCapacity() : 2;

                        return ChildRoomDto.builder()
                                .id(r.getId())
                                .roomNumber(r.getRoomNumber())
                                .name(r.getName())
                                .description(r.getDescription())
                                .floor(r.getFloor())
                                .status(r.getStatus() != null ? r.getStatus().name() : "AVAILABLE")
                                .roomTypeId(rtId)
                                .roomTypeName(rtName)
                                .bedType(bType)
                                .adults(adults)
                                .children(children)
                                .capacity(capacity)
                                .build();
                    })
                    .collect(Collectors.toList());

            for (ChildRoomDto cr : childRooms) {
                calculatedAdults += cr.getAdults() != null ? cr.getAdults() : 0;
                calculatedChildren += cr.getChildren() != null ? cr.getChildren() : 0;
                calculatedCapacity += cr.getCapacity() != null ? cr.getCapacity() : 0;
            }
        }

        if (childRooms.isEmpty() && entity.getVillaType() != null) {
            calculatedAdults = entity.getVillaType().getAdults() != null ? entity.getVillaType().getAdults() : 0;
            calculatedChildren = entity.getVillaType().getChildren() != null ? entity.getVillaType().getChildren() : 0;
            calculatedCapacity = entity.getVillaType().getCapacity() != null ? entity.getVillaType().getCapacity() : (calculatedAdults + calculatedChildren);
        }

        List<String> imageUrls = new ArrayList<>();
        if (entity.getImages() != null) {
            imageUrls = entity.getImages().stream()
                    .map(VillaImage::getImageUrl)
                    .collect(Collectors.toList());
        }

        String primaryImg = null;
        if (!imageUrls.isEmpty()) {
            primaryImg = imageUrls.get(0);
        } else if (entity.getVillaType() != null) {
            primaryImg = entity.getVillaType().getImageUrl();
        }

        List<String> parsedAmenities = new ArrayList<>();
        if (entity.getAmenities() != null && !entity.getAmenities().trim().isEmpty()) {
            parsedAmenities = Arrays.stream(entity.getAmenities().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        int bedroomCount = entity.getBedroomCount() != null ? entity.getBedroomCount() : childRooms.size();
        int totalBeds = !childRooms.isEmpty() ? childRooms.size() : (entity.getBedroomCount() != null ? entity.getBedroomCount() : 1);

        return VillaResponse.builder()
                .id(entity.getId())
                .villaNumber(entity.getVillaNumber())
                .floor(entity.getFloor())
                .structureType(entity.getStructureType())
                .basePrice(entity.getBasePrice() != null ? entity.getBasePrice() : (entity.getVillaType() != null ? entity.getVillaType().getBasePrice() : BigDecimal.ZERO))
                .status(entity.getStatus())
                .villaTypeId(entity.getVillaType() != null ? entity.getVillaType().getId() : null)
                .villaTypeName(entity.getVillaType() != null ? entity.getVillaType().getName() : null)
                .zone(entity.getZone())
                .ozoneStatus(entity.getOzoneStatus())
                .amenities(parsedAmenities)
                .lastCleanedAt(entity.getLastCleanedAt())
                .currentGuestName(entity.getCurrentGuestName())
                .bedroomCount(bedroomCount)
                .totalBeds(totalBeds)
                .totalAdults(calculatedAdults)
                .totalChildren(calculatedChildren)
                .totalCapacity(calculatedCapacity > 0 ? calculatedCapacity : (calculatedAdults + calculatedChildren))
                .imageUrl(primaryImg)
                .images(imageUrls)
                .rooms(childRooms)
                .build();
    }

    // Backward compatibility aliases for legacy Angular models
    public String getRoomNumber() {
        return villaNumber;
    }

    public Long getRoomTypeId() {
        return villaTypeId;
    }

    public String getRoomTypeName() {
        return villaTypeName;
    }
}
