package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignHousekeepingTaskRequest {
    @NotNull(message = "Mã phòng không được để trống")
    private Long roomId;

    @NotNull(message = "Mã nhân viên buồng phòng không được để trống")
    private Long housekeeperId;

    private String taskType = "CHECKOUT_DEEP"; // CHECKOUT_DEEP, DAILY, TURNDOWN
    private String notes;
}
