package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse implements Serializable {

    // 1. Core KPIs
    private Double occupancyRate;
    private String occupancyMoM;
    private Integer occupiedVillas;
    private Integer totalVillas;
    private String seasonStatus;

    private String todayRevenue;
    private Integer revenueTargetPercent;
    private String adr;
    private String revpar;

    private Integer vipInHouseCount;
    private Integer anniversaryCouplesCount;
    private String butlerCoverage;

    private Double csatRating;
    private Integer fiveStarReviewsCount;
    private Integer unresolvedComplaintsCount;

    // 2. 7-day Trend Analysis
    private List<RevenueTrendPoint> revenueTrend;
    private String forecastNext3Days;
    private String highestSegment;
    private String primaryChannel;

    // 3. Field Dispatch (Quản gia & Biệt thự hiện trường)
    private String weatherCondition;
    private List<FieldButlerDispatch> fieldDispatches;

    // 4. Matrix 10 Modules Status
    private List<ModuleMatrixStatus> moduleStatuses;

    // 5. VIP Arrivals & Departures Log
    private List<VipArrivalDeparture> vipArrivalDepartures;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueTrendPoint implements Serializable {
        private String day; // T2, T3, T4, T5, T6, T7, CN
        private Double revenueMillion;
        private Double occupancyPercent;
        private Boolean isToday;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldButlerDispatch implements Serializable {
        private String id;
        private String initials;
        private String butlerName;
        private String villaAssignment;
        private String task;
        private String statusBadge;
        private String badgeColor;
        private Boolean isGpsActive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleMatrixStatus implements Serializable {
        private Integer index;
        private String name;
        private String statusText;
        private String badge;
        private String badgeColor;
        private String icon;
        private String highlightInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipArrivalDeparture implements Serializable {
        private String id;
        private String initials;
        private String guestName;
        private String tier;
        private String tierBadgeColor;
        private String assignedVilla;
        private String flightOrRoute;
        private String transport;
        private String butler;
        private String specialRequest;
        private String status;
        private String statusColor;
        private Boolean isArrival;
    }
}
