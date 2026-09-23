package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.DayEndClosing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayEndClosingResponse {
    private Long id;
    private LocalDate closingDate;
    private BigDecimal totalRevenue;
    private BigDecimal roomRevenue;
    private BigDecimal serviceRevenue;
    private BigDecimal totalOpex;
    private BigDecimal netCash;
    private Double occupancyRate;
    private BigDecimal adr;
    private BigDecimal revPar;
    private Integer totalBookings;
    private Integer occupiedRooms;
    private String closedByName;
    private LocalDateTime closedAt;
    private String status;
    private String notes;

    public static DayEndClosingResponse fromEntity(DayEndClosing entity) {
        return DayEndClosingResponse.builder()
                .id(entity.getId())
                .closingDate(entity.getClosingDate())
                .totalRevenue(entity.getTotalRevenue())
                .roomRevenue(entity.getRoomRevenue())
                .serviceRevenue(entity.getServiceRevenue())
                .totalOpex(entity.getTotalOpex())
                .netCash(entity.getNetCash())
                .occupancyRate(entity.getOccupancyRate())
                .adr(entity.getAdr())
                .revPar(entity.getRevPar())
                .totalBookings(entity.getTotalBookings())
                .occupiedRooms(entity.getOccupiedRooms())
                .closedByName(entity.getClosedBy() != null ? entity.getClosedBy().getFullName() : null)
                .closedAt(entity.getClosedAt())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .build();
    }
}
