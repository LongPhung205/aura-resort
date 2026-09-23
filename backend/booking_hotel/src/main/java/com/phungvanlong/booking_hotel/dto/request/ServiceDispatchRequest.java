package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceDispatchRequest {
    private Long bookingId;
    @NotBlank(message = "Loại dịch vụ không được để trống")
    private String serviceType;
    private String assetCode;
    private String guestName;
    private String roomNumber;
    private Long staffId;
    private String pickupLocation;
    private String destination;
    @NotNull(message = "Thời gian thực hiện không được để trống")
    private LocalDateTime scheduledTime;
    private String flightNumber;
    private BigDecimal cost;
    private String notes;
}
