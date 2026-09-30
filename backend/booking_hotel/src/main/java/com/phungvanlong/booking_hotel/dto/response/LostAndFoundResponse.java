package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.LostAndFoundItem;
import com.phungvanlong.booking_hotel.entity.LostAndFoundStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostAndFoundResponse {
    private Long id;
    private String itemCode;
    private Long villaId;
    private String villaName;
    private Long roomId;
    private String roomNumber;
    private Long bookingId;
    private String itemName;
    private String category;
    private String foundLocation;
    private String photoUrl;
    private String finderName;
    private String guestName;
    private String guestPhone;
    private LostAndFoundStatus status;
    private String storageLocation;
    private LocalDateTime returnedAt;
    private String note;
    private LocalDateTime createdAt;

    public static LostAndFoundResponse fromEntity(LostAndFoundItem item) {
        if (item == null) return null;
        return LostAndFoundResponse.builder()
                .id(item.getId())
                .itemCode(item.getItemCode())
                .villaId(item.getVilla() != null ? item.getVilla().getId() : null)
                .villaName(item.getVilla() != null ? item.getVilla().getVillaNumber() : null)
                .roomId(item.getRoom() != null ? item.getRoom().getId() : null)
                .roomNumber(item.getRoom() != null ? item.getRoom().getRoomNumber() : null)
                .bookingId(item.getBooking() != null ? item.getBooking().getId() : null)
                .itemName(item.getItemName())
                .category(item.getCategory())
                .foundLocation(item.getFoundLocation())
                .photoUrl(item.getPhotoUrl())
                .finderName(item.getFinderName())
                .guestName(item.getGuestName())
                .guestPhone(item.getGuestPhone())
                .status(item.getStatus())
                .storageLocation(item.getStorageLocation())
                .returnedAt(item.getReturnedAt())
                .note(item.getNote())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
