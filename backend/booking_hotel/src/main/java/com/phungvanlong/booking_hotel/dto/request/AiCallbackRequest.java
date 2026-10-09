package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCallbackRequest {

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String name;

    private String note;

    private String preferredTime;
}
