package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CollectionRequest {
    @NotBlank(message = "Tên bộ sưu tập không được để trống")
    private String name;
    
    private String slug;
    private String description;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isActive;
}
