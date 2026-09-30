package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.RoomTypeRequest;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.RoomTypeResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RoomTypeService {
    RoomTypeResponse createRoomType(RoomTypeRequest request);
    RoomTypeResponse getRoomTypeById(Long id);
    List<RoomTypeResponse> getAllRoomTypes();
    RoomTypeResponse updateRoomType(Long id, RoomTypeRequest request);
    void deleteRoomType(Long id);
    
    PageResponse<RoomTypeResponse> searchRoomTypes(
            LocalDate checkInDate, LocalDate checkOutDate, 
            Integer quantity, Integer capacity, 
            BigDecimal minPrice, BigDecimal maxPrice, 
            int page, int size);
}
