package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryStatsDto {
    private long totalUsers;
    private long totalCustomers;
    private long totalStaff;
    private long totalLocked;
}
