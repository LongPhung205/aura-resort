package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRefillTaskRequest {

    @NotNull(message = "ID Villa không được để trống")
    private Long villaId;

    private Long housekeepingTaskId;

    private String assignedStaff;

    private String note;

    @NotEmpty(message = "Danh sách vật tư kiểm kê không được để trống")
    @Valid
    private List<RefillItemCheckRequest> items;
}
