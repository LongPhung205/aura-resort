package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ReviewRequest;
import com.phungvanlong.booking_hotel.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, String userEmail);
    List<ReviewResponse> getReviewsByRoomType(Long roomTypeId);
}
