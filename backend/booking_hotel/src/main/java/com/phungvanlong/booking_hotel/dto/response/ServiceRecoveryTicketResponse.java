package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.ServiceRecoveryTicket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecoveryTicketResponse {
    private Long id;
    private Long reviewId;
    private String guestName;
    private String roomNumber;
    private String incidentCategory;
    private String issueSummary;
    private String resolutionAction;
    private String assignedManagerName;
    private Integer slaMinutes;
    private Integer actualResolutionMinutes;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public static ServiceRecoveryTicketResponse fromEntity(ServiceRecoveryTicket ticket) {
        return ServiceRecoveryTicketResponse.builder()
                .id(ticket.getId())
                .reviewId(ticket.getReview() != null ? ticket.getReview().getId() : null)
                .guestName(ticket.getGuestName())
                .roomNumber(ticket.getRoomNumber())
                .incidentCategory(ticket.getIncidentCategory())
                .issueSummary(ticket.getIssueSummary())
                .resolutionAction(ticket.getResolutionAction())
                .assignedManagerName(ticket.getAssignedManager() != null ? ticket.getAssignedManager().getFullName() : null)
                .slaMinutes(ticket.getSlaMinutes())
                .actualResolutionMinutes(ticket.getActualResolutionMinutes())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}
