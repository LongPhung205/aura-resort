package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.AssignHousekeepingTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.HousekeepingChecklistRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateCleaningProgressRequest;
import com.phungvanlong.booking_hotel.dto.response.HousekeeperSummaryDto;
import com.phungvanlong.booking_hotel.dto.response.HousekeepingTaskResponse;

import java.util.List;

public interface AdminHousekeepingService {
    List<HousekeepingTaskResponse> getAllTasks(String status);
    List<HousekeepingTaskResponse> getMyTasks(String housekeeperEmail, Long housekeeperId);
    List<HousekeeperSummaryDto> getAvailableHousekeepers();
    HousekeepingTaskResponse assignTask(AssignHousekeepingTaskRequest request, String supervisorEmail);
    HousekeepingTaskResponse startCleaning(Long taskId, String housekeeperEmail);
    HousekeepingTaskResponse updateProgress(UpdateCleaningProgressRequest request, String housekeeperEmail);
    HousekeepingTaskResponse completeCleaning(Long taskId, String cleaningNote, String housekeeperEmail);
    HousekeepingTaskResponse approveTask(Long taskId, String supervisorEmail);
    HousekeepingTaskResponse startOzoneSterilisation(Long taskId);
    HousekeepingTaskResponse submitChecklist(HousekeepingChecklistRequest request);
}
