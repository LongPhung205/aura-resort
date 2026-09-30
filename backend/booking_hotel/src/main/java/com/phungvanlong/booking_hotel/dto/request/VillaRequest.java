package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.VillaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaRequest {

    @NotBlank(message = "Mã số căn Villa không được để trống")
    private String villaNumber;

    private Integer floor;
    private String structureType;
    private BigDecimal basePrice;
    private Long villaTypeId;

    private Long zoneId;
    private String zone;
    private VillaStatus status;
    private String ozoneStatus;
    private List<String> amenities;
    private Integer bedroomCount;
    private List<VillaBedSelectionDto> bedSelections;
    private List<ChildRoomRequestDto> childRooms;

    private String imageUrl;
    private List<String> images;

    private Double area;
    private String viewDirection;
    private Double poolSize;
    private String overviewDescription;

    // Backward-compatibility getters/setters for legacy clients
    public String getRoomNumber() {
        return villaNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.villaNumber = roomNumber;
    }

    public Long getRoomTypeId() {
        return villaTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.villaTypeId = roomTypeId;
    }
}
