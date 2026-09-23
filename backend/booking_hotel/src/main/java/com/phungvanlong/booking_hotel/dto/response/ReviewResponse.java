package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String userEmail;
    private String userName;
    private LocalDateTime createdAt;

    public static ReviewResponse fromEntity(Review entity) {
        return ReviewResponse.builder()
                .id(entity.getId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .userEmail(entity.getBooking().getUser().getEmail())
                .userName(entity.getBooking().getUser().getFullName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
