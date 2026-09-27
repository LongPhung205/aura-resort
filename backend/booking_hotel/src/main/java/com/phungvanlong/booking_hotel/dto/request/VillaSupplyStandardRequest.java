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
public class VillaSupplyStandardRequest {

    @NotNull(message = "ID Villa không được để trống")
    private Long villaId;

    @NotNull(message = "ID vật tư không được để trống")
    private Long itemId;

    @NotNull(message = "Định mức tiêu chuẩn không được để trống")
    @Min(value = 1, message = "Định mức tiêu chuẩn phải từ 1 trở lên")
    private Integer standardQuantity;

    private String note;
}
