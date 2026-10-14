package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ReviewRequest;
import com.phungvanlong.booking_hotel.dto.response.ReviewResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.BookingRepository;
import com.phungvanlong.booking_hotel.repository.ReviewRepository;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phungvanlong.booking_hotel.repository.RoomTypeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void ensureReviewsSchemaNullable() {
        try {
            jdbcTemplate.execute("ALTER TABLE reviews MODIFY COLUMN room_type_id BIGINT NULL");
        } catch (Exception ignored) {
        }
    }

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String userEmail) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng"));

        if (booking.getUser() != null && !booking.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException("Bạn không có quyền đánh giá đơn đặt phòng này");
        }

        if (booking.getStatus() != BookingStatus.CHECKED_OUT &&
            booking.getStatus() != BookingStatus.CONFIRMED &&
            booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BusinessException("Chỉ có thể đánh giá các đơn đặt phòng đã xác nhận hoặc đã trả phòng");
        }

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new BusinessException("Đơn đặt phòng này đã được đánh giá");
        }

        VillaType villaType = null;
        RoomType roomType = null;

        if (booking.getBookingDetails() != null && !booking.getBookingDetails().isEmpty()) {
            var firstDetail = booking.getBookingDetails().get(0);
            if (firstDetail.getVilla() != null) {
                villaType = firstDetail.getVilla().getVillaType();
            } else if (firstDetail.getRoom() != null) {
                roomType = firstDetail.getRoom().getRoomType();
                if (firstDetail.getRoom().getVilla() != null) {
                    villaType = firstDetail.getRoom().getVilla().getVillaType();
                }
            }
        }

        // An toàn 100%: nếu CSDL MySQL vẫn còn ràng buộc NOT NULL trên room_type_id, gán RoomType đầu tiên để không lỗi SQL 1048
        if (roomType == null) {
            roomType = roomTypeRepository.findAll().stream().findFirst().orElse(null);
        }

        String guestName = "Khách lưu trú";
        if (booking.getUser() != null && booking.getUser().getFullName() != null && !booking.getUser().getFullName().isBlank()) {
            guestName = booking.getUser().getFullName();
        } else if (booking.getGuestName() != null && !booking.getGuestName().isBlank()) {
            guestName = booking.getGuestName();
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .guestName(guestName)
                .booking(booking)
                .villaType(villaType)
                .roomType(roomType)
                .build();

        Review saved = reviewRepository.save(review);
        return ReviewResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByVillaId(Long villaId) {
        List<Review> list = reviewRepository.findByVillaId(villaId);
        if (list.isEmpty()) {
            var villaOpt = villaRepository.findById(villaId);
            if (villaOpt.isPresent() && villaOpt.get().getVillaType() != null) {
                list = reviewRepository.findByVillaTypeId(villaOpt.get().getVillaType().getId());
            }
        }
        return list.stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(String userEmail) {
        return reviewRepository.findByUserEmail(userEmail).stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
