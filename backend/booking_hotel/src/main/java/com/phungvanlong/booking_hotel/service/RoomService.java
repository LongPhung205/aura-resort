package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.RoomRequest;
import com.phungvanlong.booking_hotel.dto.response.RoomResponse;
import com.phungvanlong.booking_hotel.entity.RoomStatus;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(RoomRequest request);
    RoomResponse updateRoom(Long id, RoomRequest request);
    List<RoomResponse> getAllRooms(Long roomTypeId, RoomStatus status, String zone);
    List<RoomResponse> getRoomsByRoomTypeId(Long roomTypeId);
    RoomResponse updateRoomStatus(Long id, RoomStatus status);
    void deleteRoom(Long id);
}
