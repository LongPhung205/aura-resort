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
public class HomeBannerRequest {

    @NotBlank(message = "Tiêu đề banner không được để trống")
    private String title;

    private String subtitle;
    private String description;

    @NotBlank(message = "Ảnh banner không được để trống")
    private String imageUrl;

    private String mobileImageUrl;
    private String ctaText;
    private String ctaLink;
    private Integer displayOrder;
    private Boolean isActive;
    private String badgesJson;
}
