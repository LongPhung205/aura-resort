package com.phungvanlong.booking_hotel.repository.projection;

public interface HousekeeperSummaryProjection {
    Long getId();
    String getFullName();
    String getEmail();
    String getPhone();
    Long getActiveTasksCount();
}

