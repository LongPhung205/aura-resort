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
public class VillaBedSelectionDto implements Serializable {
    private Long roomTypeId;
    private Integer quantity;
}
