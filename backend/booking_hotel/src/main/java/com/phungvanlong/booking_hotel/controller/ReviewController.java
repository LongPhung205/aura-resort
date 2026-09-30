package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ReviewRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ReviewResponse;
import com.phungvanlong.booking_hotel.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        ReviewResponse review = reviewService.createReview(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(review, "Đánh giá thành công"));
    }

    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByRoomType(@PathVariable Long roomTypeId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByRoomType(roomTypeId);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá thành công"));
    }
}
