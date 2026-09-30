package com.phungvanlong.booking_hotel.dto.request;

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
public class VillaServiceRequest {
    @NotNull(message = "Villa ID không được để trống")
    private Long villaId;

    @NotNull(message = "Service ID không được để trống")
    private Long serviceId;

    /** Giá riêng cho Villa này. NULL = dùng giá mặc định */
    private BigDecimal priceOverride;

    private Boolean isAvailable;

    private String note;
}
