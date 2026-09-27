package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.VillaAsset;
import com.phungvanlong.booking_hotel.entity.VillaAssetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaAssetResponse {

    private Long id;
    private Long villaId;
    private String villaNumber;
    private String assetName;
    private String serialNumber;
    private String category;
    private VillaAssetStatus status;
    private String statusLabel;
    private LocalDate installDate;
    private LocalDate warrantyExpiry;
    private String note;
    private LocalDateTime createdAt;

    public static VillaAssetResponse fromEntity(VillaAsset asset) {
        if (asset == null) return null;

        Long villaId = null;
        String vNumber = asset.getVillaNumber();
        if (asset.getVilla() != null) {
            villaId = asset.getVilla().getId();
            vNumber = asset.getVilla().getVillaNumber();
            if (asset.getVilla().getZone() != null) {
                vNumber += " (" + asset.getVilla().getZone().getName() + ")";
            }
        }

        String label = switch (asset.getStatus()) {
            case GOOD -> "Hoạt động tốt";
            case MAINTENANCE -> "Đang bảo trì";
            case BROKEN -> "Hỏng hóc / Cần thay";
        };

        return VillaAssetResponse.builder()
                .id(asset.getId())
                .villaId(villaId)
                .villaNumber(vNumber)
                .assetName(asset.getAssetName())
                .serialNumber(asset.getSerialNumber())
                .category(asset.getCategory())
                .status(asset.getStatus())
                .statusLabel(label)
                .installDate(asset.getInstallDate())
                .warrantyExpiry(asset.getWarrantyExpiry())
                .note(asset.getNote())
                .createdAt(asset.getCreatedAt())
                .build();
    }
}
