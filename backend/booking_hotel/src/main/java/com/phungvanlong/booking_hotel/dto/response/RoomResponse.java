package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private Integer floor;
    private RoomStatus status;
    private Long roomTypeId;
    private String roomTypeName;
    private Long zoneId;
    private String zone;
    private String ozoneStatus;
    private java.time.LocalDateTime lastCleanedAt;
    private String currentGuestName;

    public static RoomResponse fromEntity(Room entity) {
        Long zId = entity.getZone() != null ? entity.getZone().getId() : (entity.getVilla() != null && entity.getVilla().getZone() != null ? entity.getVilla().getZone().getId() : null);
        String zName = entity.getZone() != null ? entity.getZone().getName() : (entity.getVilla() != null && entity.getVilla().getZone() != null ? entity.getVilla().getZone().getName() : null);

        return RoomResponse.builder()
                .id(entity.getId())
                .roomNumber(entity.getRoomNumber())
                .floor(entity.getFloor())
                .status(entity.getStatus())
                .roomTypeId(entity.getRoomType() != null ? entity.getRoomType().getId() : null)
                .roomTypeName(entity.getRoomType() != null ? entity.getRoomType().getName() : null)
                .zoneId(zId)
                .zone(zName)
                .ozoneStatus(entity.getOzoneStatus())
                .lastCleanedAt(entity.getLastCleanedAt())
                .currentGuestName(entity.getCurrentGuestName())
                .build();
    }
}
