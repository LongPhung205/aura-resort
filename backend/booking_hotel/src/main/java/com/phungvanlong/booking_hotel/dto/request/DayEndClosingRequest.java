package com.phungvanlong.booking_hotel.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DayEndClosingRequest {
    private LocalDate closingDate;
    private String notes;
}
