package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "Số phòng không được để trống")
    private String roomNumber;

    @NotNull(message = "Tầng không được để trống")
    private Integer floor;

    @NotNull(message = "ID Hạng phòng không được để trống")
    private Long roomTypeId;

    private String zone;
    private com.phungvanlong.booking_hotel.entity.RoomStatus status;
    private String ozoneStatus;
}
