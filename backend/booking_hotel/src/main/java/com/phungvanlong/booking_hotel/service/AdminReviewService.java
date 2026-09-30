package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ServiceRecoveryRequest;
import com.phungvanlong.booking_hotel.dto.response.ReviewAnalyticsResponse;
import com.phungvanlong.booking_hotel.dto.response.ReviewResponse;
import com.phungvanlong.booking_hotel.dto.response.ServiceRecoveryTicketResponse;

public interface AdminReviewService {
    ReviewAnalyticsResponse getAnalytics();
    ReviewResponse replyReview(Long reviewId, String reply, String managerEmail);
    ServiceRecoveryTicketResponse createRecoveryTicket(ServiceRecoveryRequest request, String managerEmail);
    ServiceRecoveryTicketResponse resolveRecoveryTicket(Long ticketId, Integer resolutionMinutes);
}
