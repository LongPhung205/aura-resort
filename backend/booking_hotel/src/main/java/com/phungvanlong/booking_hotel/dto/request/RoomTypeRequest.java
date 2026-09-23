package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeRequest {

    @NotBlank(message = "Tên hạng phòng không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá cơ bản không được để trống")
    @Min(value = 0, message = "Giá không được nhỏ hơn 0")
    private BigDecimal basePrice;

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private Integer capacity;

    private Integer adults;

    private Integer children;

    private String bedType;

    private String imageUrl;

    private List<String> images;
}
