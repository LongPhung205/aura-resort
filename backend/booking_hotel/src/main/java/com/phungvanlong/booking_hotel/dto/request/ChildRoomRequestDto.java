package com.phungvanlong.booking_hotel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildRoomRequestDto implements Serializable {
    private String roomNumber;
    private String name;
    private Integer floor;
    private Long roomTypeId;
    private String description;
}
