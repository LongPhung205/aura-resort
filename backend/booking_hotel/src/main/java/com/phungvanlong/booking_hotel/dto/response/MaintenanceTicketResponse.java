package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.MaintenancePriority;
import com.phungvanlong.booking_hotel.entity.MaintenanceStatus;
import com.phungvanlong.booking_hotel.entity.MaintenanceTicket;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTicketResponse {
    private Long id;
    private String ticketCode;
    private Long villaId;
    private String villaName;
    private Long roomId;
    private String roomNumber;
    private String category;
    private MaintenancePriority priority;
    private String description;
    private String photoUrl;
    private MaintenanceStatus status;
    private String reportedBy;
    private String technicianName;
    private LocalDateTime resolvedAt;
    private String technicianNote;
    private LocalDateTime createdAt;

    public static MaintenanceTicketResponse fromEntity(MaintenanceTicket ticket) {
        if (ticket == null) return null;
        return MaintenanceTicketResponse.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .villaId(ticket.getVilla() != null ? ticket.getVilla().getId() : null)
                .villaName(ticket.getVilla() != null ? ticket.getVilla().getVillaNumber() : null)
                .roomId(ticket.getRoom() != null ? ticket.getRoom().getId() : null)
                .roomNumber(ticket.getRoom() != null ? ticket.getRoom().getRoomNumber() : null)
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .description(ticket.getDescription())
                .photoUrl(ticket.getPhotoUrl())
                .status(ticket.getStatus())
                .reportedBy(ticket.getReportedBy())
                .technicianName(ticket.getTechnicianName())
                .resolvedAt(ticket.getResolvedAt())
                .technicianNote(ticket.getTechnicianNote())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
