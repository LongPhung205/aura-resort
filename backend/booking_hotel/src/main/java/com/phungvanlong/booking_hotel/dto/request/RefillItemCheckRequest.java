package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefillItemCheckRequest {

    @NotNull(message = "ID vật tư không được để trống")
    private Long itemId;

    @NotNull(message = "Số lượng thực tế còn lại không được để trống")
    @Min(value = 0, message = "Số lượng thực tế không được âm")
    private Integer actualQuantity;

    @Min(value = 0, message = "Số lượng tiêu thụ không được âm")
    private Integer consumedQuantity;

    @Min(value = 0, message = "Số lượng hư hỏng không được âm")
    private Integer damagedQuantity;

    @Min(value = 0, message = "Số lượng thất thoát không được âm")
    private Integer missingQuantity;
}
