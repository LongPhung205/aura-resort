package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.ComboPackage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComboPackageResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String status;
    private java.util.List<ExtraServiceDto> extraServices;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtraServiceDto {
        private Long id;
        private String name;
        private String icon;
        private BigDecimal price;
    }

    public static ComboPackageResponse fromEntity(ComboPackage entity) {
        java.util.List<ExtraServiceDto> services = java.util.Collections.emptyList();
        if (entity.getExtraServices() != null) {
            services = entity.getExtraServices().stream()
                .map(s -> ExtraServiceDto.builder()
                    .id(s.getId())
                    .name(s.getName())
                    .icon(s.getIcon())
                    .price(s.getPrice())
                    .build())
                .collect(java.util.stream.Collectors.toList());
        }

        return ComboPackageResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .extraServices(services)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
