package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.ServiceDispatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDispatchResponse {
    private Long id;
    private Long bookingId;
    private String bookingCode;
    private String serviceType;
    private String assetCode;
    private String guestName;
    private String roomNumber;
    private String staffName;
    private String pickupLocation;
    private String destination;
    private LocalDateTime scheduledTime;
    private String flightNumber;
    private String status;
    private BigDecimal cost;
    private String notes;

    public static ServiceDispatchResponse fromEntity(ServiceDispatch entity) {
        return ServiceDispatchResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBooking() != null ? entity.getBooking().getId() : null)
                .bookingCode(entity.getBooking() != null ? entity.getBooking().getBookingCode() : null)
                .serviceType(entity.getServiceType())
                .assetCode(entity.getAssetCode())
                .guestName(entity.getGuestName())
                .roomNumber(entity.getRoomNumber())
                .staffName(entity.getAssignedStaff() != null ? entity.getAssignedStaff().getFullName() : null)
                .pickupLocation(entity.getPickupLocation())
                .destination(entity.getDestination())
                .scheduledTime(entity.getScheduledTime())
                .flightNumber(entity.getFlightNumber())
                .status(entity.getStatus())
                .cost(entity.getCost())
                .notes(entity.getNotes())
                .build();
    }
}
