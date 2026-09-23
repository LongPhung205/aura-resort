package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceRecoveryRequest {
    private Long reviewId;
    @NotBlank(message = "Tên khách không được để trống")
    private String guestName;
    private String roomNumber;
    private String incidentCategory;
    @NotBlank(message = "Tóm tắt sự cố không được để trống")
    private String issueSummary;
    private String resolutionAction;
    private Long assignedManagerId;
}
