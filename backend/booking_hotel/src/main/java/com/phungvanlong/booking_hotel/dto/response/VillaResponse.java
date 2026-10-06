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
    private Long zoneId;
    private String zone;
    private String zoneTag;
    private String zoneIcon;
    private String zoneBadgeClass;
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
    
    private Double area;
    private String viewDirection;
    private Double poolSize;
    private String overviewDescription;
    
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
