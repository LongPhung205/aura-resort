package com.phungvanlong.booking_hotel.mapper;

import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse.ChildRoomDto;
import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaImage;
import org.hibernate.LazyInitializationException;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class VillaMapper {

    @Mapping(target = "villaTypeId", source = "villaType.id")
    @Mapping(target = "villaTypeName", source = "villaType.name")
    @Mapping(target = "zoneId", source = "zone.id")
    @Mapping(target = "zone", source = "zone.name")
    @Mapping(target = "zoneTag", source = "zone.tag")
    @Mapping(target = "zoneIcon", source = "zone.icon")
    @Mapping(target = "zoneBadgeClass", source = "zone.badgeClass")
    @Mapping(target = "basePrice", expression = "java(entity.getBasePrice() != null ? entity.getBasePrice() : (entity.getVillaType() != null ? entity.getVillaType().getBasePrice() : java.math.BigDecimal.ZERO))")
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    public abstract VillaResponse toResponse(Villa entity);

    @AfterMapping
    protected void fillCalculatedFields(Villa entity, @MappingTarget VillaResponse.VillaResponseBuilder responseBuilder) {
        if (entity == null) return;

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
        try {
            if (entity.getVillaServices() != null && !entity.getVillaServices().isEmpty()) {
                parsedAmenities = entity.getVillaServices().stream()
                        .filter(vs -> Boolean.TRUE.equals(vs.getIsAvailable()) && vs.getService() != null)
                        .map(vs -> vs.getService().getName())
                        .collect(Collectors.toList());
            }
        } catch (LazyInitializationException e) {
            // Fallback: lazy loading outside transaction → sử dụng field amenities thay thế
        }
        if (parsedAmenities.isEmpty() && entity.getAmenities() != null && !entity.getAmenities().trim().isEmpty()) {
            parsedAmenities = Arrays.stream(entity.getAmenities().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        int bedroomCount = entity.getBedroomCount() != null ? entity.getBedroomCount() : childRooms.size();
        int totalBeds = !childRooms.isEmpty() ? childRooms.size() : (entity.getBedroomCount() != null ? entity.getBedroomCount() : 1);

        responseBuilder
                .rooms(childRooms)
                .totalAdults(calculatedAdults)
                .totalChildren(calculatedChildren)
                .totalCapacity(calculatedCapacity > 0 ? calculatedCapacity : (calculatedAdults + calculatedChildren))
                .images(imageUrls)
                .imageUrl(primaryImg)
                .amenities(parsedAmenities)
                .bedroomCount(bedroomCount)
                .totalBeds(totalBeds);
    }
}
