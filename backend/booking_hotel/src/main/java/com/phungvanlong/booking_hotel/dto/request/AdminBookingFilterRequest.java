package com.phungvanlong.booking_hotel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingFilterRequest {
    private String search;
    private String roomType;
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String quickFilter; // "all", "vip", "butler", "maybach", "unassigned"

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sortBy = "checkInDate";

    @Builder.Default
    private String sortDirection = "ASC";
}
