package com.phungvanlong.booking_hotel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDirectBookingRequest {
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private String villaNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal amount;
    private String paymentMethod;
    private String note;
}
