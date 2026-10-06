package com.phungvanlong.booking_hotel.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComboPackageRequest {
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String status;
    private java.util.List<Long> extraServiceIds;
}
