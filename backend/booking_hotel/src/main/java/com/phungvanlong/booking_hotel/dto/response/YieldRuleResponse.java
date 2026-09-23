package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.YieldRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YieldRuleResponse implements Serializable {
    private Long id;
    private String ruleName;
    private String conditionType;
    private Double thresholdValue;
    private Double priceMultiplier;
    private String targetVillaTypes;
    private String targetRoomTypes;
    private Boolean isActive;
    private String description;

    public static YieldRuleResponse fromEntity(YieldRule entity) {
        String targets = entity.getTargetVillaTypes() != null ? entity.getTargetVillaTypes() : entity.getTargetRoomTypes();
        return YieldRuleResponse.builder()
                .id(entity.getId())
                .ruleName(entity.getRuleName())
                .conditionType(entity.getConditionType())
                .thresholdValue(entity.getThresholdValue())
                .priceMultiplier(entity.getPriceMultiplier())
                .targetVillaTypes(targets)
                .targetRoomTypes(targets)
                .isActive(entity.getIsActive())
                .description(entity.getDescription())
                .build();
    }
}
