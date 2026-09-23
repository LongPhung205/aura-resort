package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HousekeepingChecklistRequest {
    @NotNull(message = "Task ID không được để trống")
    private Long taskId;
    private String checklistJson; // 16 criteria
    private String evidencePhotoUrl;
    private String notes;
}
