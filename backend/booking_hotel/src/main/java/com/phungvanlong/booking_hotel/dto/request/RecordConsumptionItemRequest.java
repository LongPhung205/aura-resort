package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.RoomConsumptionItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordConsumptionItemRequest {

    @NotNull(message = "Loại hạng mục không được để trống")
    private RoomConsumptionItemType itemType;

    @NotBlank(message = "Tên món đồ không được để trống")
    private String itemName;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;

    @NotNull(message = "Đơn giá không được để trống")
    private BigDecimal unitPrice;

    private String evidencePhotoUrl;

    private String note;
}
