package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.RefillTask;
import com.phungvanlong.booking_hotel.entity.RefillTaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefillTaskResponse {

    private Long id;
    private String taskCode;
    private Long villaId;
    private String villaNumber;
    private Long housekeepingTaskId;
    private RefillTaskStatus status;
    private String statusLabel;
    private String creator;
    private String assignedStaff;
    private LocalDateTime completedAt;
    private String note;
    private LocalDateTime createdAt;
    private int totalItemsCount;
    private int totalRefillUnits;
    private List<RefillTaskItemResponse> items;

    public static RefillTaskResponse fromEntity(RefillTask entity) {
        if (entity == null) return null;

        String vNumber = entity.getVilla() != null ? entity.getVilla().getVillaNumber() : null;
        if (entity.getVilla() != null && entity.getVilla().getZone() != null) {
            vNumber += " (" + entity.getVilla().getZone().getName() + ")";
        }

        String label = switch (entity.getStatus()) {
            case PENDING -> "Chờ cấp phát";
            case IN_PROGRESS -> "Đang bổ sung";
            case COMPLETED -> "Đã hoàn tất";
            case CANCELLED -> "Đã hủy";
        };

        List<RefillTaskItemResponse> itemResponses = (entity.getItems() != null)
                ? entity.getItems().stream().map(RefillTaskItemResponse::fromEntity).collect(Collectors.toList())
                : Collections.emptyList();

        int sumRefill = itemResponses.stream()
                .mapToInt(i -> i.getRefillQuantity() != null ? i.getRefillQuantity() : 0)
                .sum();

        return RefillTaskResponse.builder()
                .id(entity.getId())
                .taskCode(entity.getTaskCode())
                .villaId(entity.getVilla() != null ? entity.getVilla().getId() : null)
                .villaNumber(vNumber)
                .housekeepingTaskId(entity.getHousekeepingTask() != null ? entity.getHousekeepingTask().getId() : null)
                .status(entity.getStatus())
                .statusLabel(label)
                .creator(entity.getCreator())
                .assignedStaff(entity.getAssignedStaff())
                .completedAt(entity.getCompletedAt())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .totalItemsCount(itemResponses.size())
                .totalRefillUnits(sumRefill)
                .items(itemResponses)
                .build();
    }
}
