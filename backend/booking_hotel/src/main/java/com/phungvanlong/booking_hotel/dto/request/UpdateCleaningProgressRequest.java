package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCleaningProgressRequest {
    @NotNull(message = "Mã nhiệm vụ không được để trống")
    private Long taskId;
    private String status; // IN_PROGRESS, INSPECTED
    private String checklistJson;
    private String cleaningNote;
    private String evidencePhotoUrl;
}
