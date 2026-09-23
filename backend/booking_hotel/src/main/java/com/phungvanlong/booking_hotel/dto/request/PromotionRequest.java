package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.DiscountType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    private String code;

    @NotNull(message = "Loại khuyến mãi không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị khuyến mãi không được để trống")
    @Min(value = 0, message = "Giá trị khuyến mãi phải lớn hơn hoặc bằng 0")
    private BigDecimal discountValue;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải ở tương lai")
    private LocalDate endDate;

    private Integer quantity;
}
