package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.MaintenancePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTicketRequest {

    @NotNull(message = "ID Villa không được để trống")
    private Long villaId;

    private Long roomId;

    @NotBlank(message = "Danh mục hư hỏng không được để trống")
    private String category;

    @Builder.Default
    private MaintenancePriority priority = MaintenancePriority.MEDIUM;

    @NotBlank(message = "Mô tả sự cố không được để trống")
    private String description;

    private String photoUrl;
}
