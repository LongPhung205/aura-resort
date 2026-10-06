package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveWeeklyRegistrationRequest {

    @NotNull(message = "registrationId không được để trống")
    private Long registrationId;

    @NotNull(message = "approved không được để trống")
    private Boolean approved;

    private String rejectionReason;
}
