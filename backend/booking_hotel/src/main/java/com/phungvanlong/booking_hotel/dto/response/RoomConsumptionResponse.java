package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.RoomConsumptionItemType;
import com.phungvanlong.booking_hotel.entity.RoomConsumptionRecord;
import com.phungvanlong.booking_hotel.entity.RoomConsumptionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomConsumptionResponse {
    private Long id;
    private Long bookingId;
    private String bookingCode;
    private Long housekeepingTaskId;
    private Long villaId;
    private String villaName;
    private Long roomId;
    private String roomNumber;
    private RoomConsumptionItemType itemType;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private RoomConsumptionStatus status;
    private String evidencePhotoUrl;
    private String note;
    private String recordedBy;
    private String approvedBy;
    private LocalDateTime createdAt;

    public static RoomConsumptionResponse fromEntity(RoomConsumptionRecord record) {
        if (record == null) return null;
        return RoomConsumptionResponse.builder()
                .id(record.getId())
                .bookingId(record.getBooking() != null ? record.getBooking().getId() : null)
                .bookingCode(record.getBooking() != null ? record.getBooking().getBookingCode() : null)
                .housekeepingTaskId(record.getHousekeepingTask() != null ? record.getHousekeepingTask().getId() : null)
                .villaId(record.getVilla() != null ? record.getVilla().getId() : null)
                .villaName(record.getVilla() != null ? record.getVilla().getVillaNumber() : null)
                .roomId(record.getRoom() != null ? record.getRoom().getId() : null)
                .roomNumber(record.getRoom() != null ? record.getRoom().getRoomNumber() : null)
                .itemType(record.getItemType())
                .itemName(record.getItemName())
                .quantity(record.getQuantity())
                .unitPrice(record.getUnitPrice())
                .totalPrice(record.getTotalPrice())
                .status(record.getStatus())
                .evidencePhotoUrl(record.getEvidencePhotoUrl())
                .note(record.getNote())
                .recordedBy(record.getRecordedBy())
                .approvedBy(record.getApprovedBy())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
