package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.repository.projection.HousekeeperSummaryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HousekeeperSummaryDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Long activeTasksCount;

    public static HousekeeperSummaryDto fromProjection(HousekeeperSummaryProjection projection) {
        if (projection == null) {
            return null;
        }
        return HousekeeperSummaryDto.builder()
                .id(projection.getId())
                .fullName(projection.getFullName())
                .email(projection.getEmail())
                .phone(projection.getPhone())
                .activeTasksCount(projection.getActiveTasksCount())
                .build();
    }
}
