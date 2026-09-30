package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneRequest {

    @NotBlank(message = "Tên phân khu không được để trống")
    private String name;

    private String tag;
    private String icon;
    private String badgeClass;
    private String description;
    private String slug;
    private String bannerUrl;
    private String highlights;
    private Integer displayOrder;
    private Boolean isActive;
}
