package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyShiftRegistrationRequest {

    @NotNull(message = "weekStartDate không được để trống")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekStartDate;

    private String preferredZone;

    private String notes;

    @NotEmpty(message = "Danh sách ca trực không được để trống")
    private List<DayRegistrationItem> days;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayRegistrationItem {
        @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate workDate;
        private String dayOfWeek; // T2, T3, T4, T5, T6, T7, CN
        private String shiftType; // MORNING, AFTERNOON, NIGHT, OFF
        private String note;
    }
}
