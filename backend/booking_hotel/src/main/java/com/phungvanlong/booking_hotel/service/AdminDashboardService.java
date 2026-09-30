package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.response.DashboardStatsResponse;

public interface AdminDashboardService {
    DashboardStatsResponse getDashboardStats(String resortId, String period);
}
