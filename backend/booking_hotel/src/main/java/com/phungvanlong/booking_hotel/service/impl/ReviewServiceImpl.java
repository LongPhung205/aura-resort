package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ReviewRequest;
import com.phungvanlong.booking_hotel.dto.response.ReviewResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.BookingRepository;
import com.phungvanlong.booking_hotel.repository.ReviewRepository;
import com.phungvanlong.booking_hotel.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request, String userEmail) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException("Bạn không có quyền đánh giá đơn đặt phòng này");
        }

        if (booking.getStatus() != BookingStatus.CHECKED_OUT) {
            throw new BusinessException("Chỉ có thể đánh giá sau khi đã Check-out");
        }

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new BusinessException("Đơn đặt phòng này đã được đánh giá");
        }

        if (booking.getBookingDetails() == null || booking.getBookingDetails().isEmpty()) {
            throw new BusinessException("Đơn đặt phòng không hợp lệ (không có chi tiết phòng/villa)");
        }
        
        var firstDetail = booking.getBookingDetails().get(0);
        VillaType villaType = null;
        RoomType roomType = null;

        if (firstDetail.getVilla() != null) {
            villaType = firstDetail.getVilla().getVillaType();
        } else if (firstDetail.getRoom() != null) {
            roomType = firstDetail.getRoom().getRoomType();
            if (firstDetail.getRoom().getVilla() != null) {
                villaType = firstDetail.getRoom().getVilla().getVillaType();
            }
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .guestName(booking.getUser() != null ? booking.getUser().getFullName() : "Khách lưu trú")
                .booking(booking)
                .villaType(villaType)
                .roomType(roomType)
                .build();

        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> getReviewsByRoomType(Long roomTypeId) {
        List<Review> list = reviewRepository.findByVillaTypeId(roomTypeId);
        if (list.isEmpty()) {
            list = reviewRepository.findByRoomTypeId(roomTypeId);
        }
        return list.stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getMyReviews(String userEmail) {
        return reviewRepository.findByUserEmail(userEmail).stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
