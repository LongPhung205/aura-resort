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
    private Long bookingId;
    private String bookingCode;
    private String villaName;
    private String sentiment;
    private String managementReply;
    private LocalDateTime repliedAt;

    public static ReviewResponse fromEntity(Review entity) {
        String userEmail = null;
        String userName = null;
        Long bookingId = null;
        String bookingCode = null;
        String villaName = "Biệt Thự Nghỉ Dưỡng Aura";

        if (entity.getBooking() != null) {
            bookingId = entity.getBooking().getId();
            bookingCode = entity.getBooking().getBookingCode();
            if (entity.getBooking().getUser() != null) {
                userEmail = entity.getBooking().getUser().getEmail();
                userName = entity.getBooking().getUser().getFullName();
            }
            if (entity.getBooking().getBookingDetails() != null && !entity.getBooking().getBookingDetails().isEmpty()) {
                var bd = entity.getBooking().getBookingDetails().get(0);
                if (bd.getVilla() != null) {
                    if (bd.getVilla().getVillaType() != null && bd.getVilla().getVillaType().getName() != null) {
                        villaName = bd.getVilla().getVillaType().getName() + " (" + bd.getVilla().getVillaNumber() + ")";
                    } else {
                        villaName = "Villa " + bd.getVilla().getVillaNumber();
                    }
                } else if (bd.getRoom() != null && bd.getRoom().getRoomType() != null) {
                    villaName = bd.getRoom().getRoomType().getName();
                }
            }
        }

        if (entity.getVillaType() != null) {
            villaName = entity.getVillaType().getName();
        }

        return ReviewResponse.builder()
                .id(entity.getId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .userEmail(userEmail)
                .userName(userName)
                .createdAt(entity.getCreatedAt())
                .bookingId(bookingId)
                .bookingCode(bookingCode)
                .villaName(villaName)
                .sentiment(entity.getSentiment())
                .managementReply(entity.getManagementReply())
                .repliedAt(entity.getRepliedAt())
                .build();
    }
}
