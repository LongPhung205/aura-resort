package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.CreateRefillTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaSupplyStandardRequest;
import com.phungvanlong.booking_hotel.dto.response.RefillTaskResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaInventoryResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaSupplyStandardResponse;
import com.phungvanlong.booking_hotel.entity.RefillTaskStatus;

import java.util.List;

public interface AdminRefillService {

    List<VillaSupplyStandardResponse> getStandardsByVilla(Long villaId);

    VillaSupplyStandardResponse saveStandard(VillaSupplyStandardRequest request);

    void deleteStandard(Long id);

    void applyDefaultStandardsToVilla(Long villaId);
    
    void applyDefaultStandardsToAllVillas();

    List<VillaInventoryResponse> getVillaInventory(Long villaId);

    RefillTaskResponse createRefillTask(CreateRefillTaskRequest request, String creatorEmail);

    RefillTaskResponse fulfillRefillTask(Long taskId, String performerEmail);

    List<RefillTaskResponse> getRefillTasks(RefillTaskStatus status, Long villaId);

    RefillTaskResponse getRefillTaskById(Long id);
}
