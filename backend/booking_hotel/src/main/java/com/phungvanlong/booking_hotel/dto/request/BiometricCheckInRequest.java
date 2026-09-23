package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BiometricCheckInRequest {
    @NotNull(message = "Staff ID không được để trống")
    private Long staffId;
    private String checkType; // CHECK_IN, CHECK_OUT
    private String method; // FACE_ID, GPS, RFID
    private String locationName;
    private Double latitude;
    private Double longitude;
    private Double matchAccuracy;
    private String photoUrl;
}
