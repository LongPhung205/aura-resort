package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingItemResponse implements Serializable {
    private Long id;
    private String bookingCode;
    private String bookingDateFormatted;

    // Guest Profile
    private String guestName;
    private String avatarUrl;
    private String guestCountry;
    private String guestPhone;
    private String guestTier; // Diamond, Black Elite, Platinum, Gold Elite
    private String tierBadgeColor;

    // Villa Details
    private String villaNumber;
    private String villaTypeName;
    private String roomTypeName;

    public String getVillaTypeName() {
        return villaTypeName != null ? villaTypeName : roomTypeName;
    }

    public String getRoomTypeName() {
        return roomTypeName != null ? roomTypeName : villaTypeName;
    }

    // Stay Details
    private String checkInFormatted;
    private String checkOutFormatted;
    private Integer nights;
    private String guestSummary; // "2 Người lớn • 1 Trẻ em"

    // Booking Channel
    private String channel; // "VIP Concierge", "Direct GM", "Booking.com Luxury", "Website Trực Tiếp"
    private String channelBadgeColor;

    // Financials & Payment
    private BigDecimal totalAmount;
    private String totalAmountDisplay;
    private String paymentStatusDisplay;
    private Boolean isFullyPaid;

    // Service & Butler
    private String extraServiceName;
    private String extraServiceIcon;
    private String assignedButler;

    // Operational Status
    private String statusCode;
    private String statusLabel;
    private String statusBadgeColor;
}
