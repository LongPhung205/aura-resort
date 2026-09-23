package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAnalyticsResponse {
    private Double csatScore; // Ví dụ: 9.92
    private Integer npsScore; // Ví dụ: +94
    private Integer totalReviews;
    private Long positiveCount;
    private Long neutralCount;
    private Long negativeCount;
    private Double responseRate; // Tỷ lệ phản hồi
    private Double averageResponseMinutes; // Thời gian phản hồi trung bình
    private List<ReviewResponse> latestReviews;
    private List<ServiceRecoveryTicketResponse> openRecoveryTickets;
}
