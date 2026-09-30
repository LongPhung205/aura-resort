package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostAndFoundRequest {

    @NotNull(message = "ID Villa không được để trống")
    private Long villaId;

    private Long roomId;

    private Long bookingId;

    @NotBlank(message = "Tên đồ vật không được để trống")
    private String itemName;

    @Builder.Default
    private String category = "OTHER";

    @NotBlank(message = "Vị trí nhặt được không được để trống")
    private String foundLocation;

    private String photoUrl;

    private String guestName;

    private String guestPhone;

    private String note;
}
