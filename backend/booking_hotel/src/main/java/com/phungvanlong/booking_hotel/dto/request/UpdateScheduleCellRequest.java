package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleCellRequest {

    @NotNull(message = "staffId không được để trống")
    private Long staffId;

    @NotNull(message = "workDate không được để trống")
    private LocalDate workDate;

    @NotNull(message = "shiftType không được để trống")
    private String shiftType; // MORNING, AFTERNOON, NIGHT, ONCALL, OFF

    private String note;
}
