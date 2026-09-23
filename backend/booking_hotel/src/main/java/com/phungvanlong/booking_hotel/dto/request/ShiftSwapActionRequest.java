package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShiftSwapActionRequest {
    @NotNull(message = "ID yêu cầu không được để trống")
    private Long requestId;
    private Boolean approved;
    private String rejectReason;
}
