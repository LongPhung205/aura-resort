package com.phungvanlong.booking_hotel.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignHousekeepingTaskRequest {
    private Long taskId;

    private Long roomId;

    private Long villaId;

    @NotNull(message = "Mã nhân viên buồng phòng không được để trống")
    private Long housekeeperId;

    private String taskType = "CHECKOUT_DEEP"; // CHECKOUT_DEEP, DAILY, TURNDOWN
    private String notes;
}
