package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.VillaTypeRequest;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaTypeResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VillaTypeService {
    VillaTypeResponse createVillaType(VillaTypeRequest request);
    VillaTypeResponse getVillaTypeById(Long id);
    List<VillaTypeResponse> getAllVillaTypes();
    VillaTypeResponse updateVillaType(Long id, VillaTypeRequest request);
    void deleteVillaType(Long id);
    
    PageResponse<VillaTypeResponse> searchVillaTypes(
            LocalDate checkInDate, LocalDate checkOutDate, 
            Integer quantity, Integer capacity, 
            BigDecimal minPrice, BigDecimal maxPrice, 
            int page, int size);
}
