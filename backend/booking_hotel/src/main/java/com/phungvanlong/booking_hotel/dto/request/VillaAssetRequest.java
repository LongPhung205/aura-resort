package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.VillaAssetStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaAssetRequest {

    private Long villaId;

    private String villaNumber;

    @NotBlank(message = "Tên tài sản / thiết bị không được để trống")
    private String assetName;

    private String serialNumber;

    private String category;

    private VillaAssetStatus status;

    private LocalDate installDate;

    private LocalDate warrantyExpiry;

    private String note;
}
