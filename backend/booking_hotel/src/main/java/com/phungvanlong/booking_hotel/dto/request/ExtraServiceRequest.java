package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraServiceRequest {
    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá dịch vụ không được để trống")
    @Min(value = 0, message = "Giá dịch vụ phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    /** DINING, TRANSPORT, SPA, ENTERTAINMENT, CLEANING, OTHER */
    private String type;

    /** người, gói, ngày, lần, chuyến */
    private String unit;

    /** Material Symbols icon name */
    private String icon;

    private String imageUrl;

    private Boolean isActive;
}
