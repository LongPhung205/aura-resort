package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.LostAndFoundRequest;
import com.phungvanlong.booking_hotel.dto.request.MaintenanceTicketRequest;
import com.phungvanlong.booking_hotel.dto.request.SubmitRoomInspectionRequest;
import com.phungvanlong.booking_hotel.dto.response.LostAndFoundResponse;
import com.phungvanlong.booking_hotel.dto.response.MaintenanceTicketResponse;
import com.phungvanlong.booking_hotel.dto.response.RoomConsumptionResponse;
import com.phungvanlong.booking_hotel.entity.LostAndFoundStatus;
import com.phungvanlong.booking_hotel.entity.MaintenanceStatus;

import java.util.List;

public interface HousekeepingInspectionService {

    List<RoomConsumptionResponse> submitInspection(Long taskId, SubmitRoomInspectionRequest request, String staffEmail);

    List<RoomConsumptionResponse> getConsumptionsByTask(Long taskId);

    List<RoomConsumptionResponse> getPendingConsumptionsByBooking(Long bookingId);

    RoomConsumptionResponse approveConsumption(Long consumptionId, String receptionistEmail);

    RoomConsumptionResponse waiveConsumption(Long consumptionId, String reason, String receptionistEmail);

    LostAndFoundResponse createLostAndFound(LostAndFoundRequest request, String finderEmail);

    List<LostAndFoundResponse> getLostAndFoundList(LostAndFoundStatus status, Long villaId);

    LostAndFoundResponse updateLostAndFoundStatus(Long id, LostAndFoundStatus status, String note);

    MaintenanceTicketResponse createMaintenanceTicket(MaintenanceTicketRequest request, String reporterEmail);

    List<MaintenanceTicketResponse> getMaintenanceTickets(MaintenanceStatus status, Long villaId);

    MaintenanceTicketResponse updateMaintenanceTicketStatus(Long id, MaintenanceStatus status, String technicianNote, String techName);
}
