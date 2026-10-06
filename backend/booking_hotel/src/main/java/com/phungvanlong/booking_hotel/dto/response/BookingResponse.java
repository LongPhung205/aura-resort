package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse implements Serializable {
    private Long id;
    private String bookingCode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime expireAt;
    private String note;
    private String userEmail;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private List<String> bookedVillaNumbers;
    private List<String> bookedRoomNumbers;

    public static BookingResponse fromEntity(Booking entity) {
        List<String> units = new ArrayList<>();
        if (entity.getBookingDetails() != null) {
            units = entity.getBookingDetails().stream()
                    .map(bd -> {
                        if (bd.getVilla() != null) return bd.getVilla().getVillaNumber();
                        if (bd.getRoom() != null) return bd.getRoom().getRoomNumber();
                        return "";
                    })
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        return BookingResponse.builder()
                .id(entity.getId())
                .bookingCode(entity.getBookingCode())
                .checkInDate(entity.getCheckInDate())
                .checkOutDate(entity.getCheckOutDate())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .expireAt(entity.getExpireAt())
                .note(entity.getNote())
                .userEmail(entity.getUser() != null ? entity.getUser().getEmail() : null)
                .guestName(entity.getGuestName())
                .guestEmail(entity.getGuestEmail())
                .guestPhone(entity.getGuestPhone())
                .bookedVillaNumbers(units)
                .bookedRoomNumbers(units)
                .build();
    }
}
