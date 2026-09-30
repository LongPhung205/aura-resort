package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.RoomRequest;
import com.phungvanlong.booking_hotel.dto.response.RoomResponse;
import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.RoomStatus;
import com.phungvanlong.booking_hotel.entity.RoomType;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.entity.Zone;
import com.phungvanlong.booking_hotel.repository.RoomRepository;
import com.phungvanlong.booking_hotel.repository.RoomTypeRepository;
import com.phungvanlong.booking_hotel.repository.ZoneRepository;
import com.phungvanlong.booking_hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ZoneRepository zoneRepository;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new BusinessException("Số phòng đã tồn tại: " + request.getRoomNumber());
        }

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hạng phòng"));

        Zone zone = resolveZone(request.getZoneId(), request.getZone());

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .floor(request.getFloor())
                .zone(zone)
                .roomType(roomType)
                .status(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE)
                .ozoneStatus(request.getOzoneStatus() != null ? request.getOzoneStatus() : "STERILIZED")
                .build();

        Room saved = roomRepository.save(room);
        return RoomResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phòng ID: " + id));

        if (!room.getRoomNumber().equalsIgnoreCase(request.getRoomNumber()) && roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new BusinessException("Số phòng mới đã tồn tại: " + request.getRoomNumber());
        }

        if (request.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy hạng phòng"));
            room.setRoomType(roomType);
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        if (request.getZoneId() != null || request.getZone() != null) {
            Zone zone = resolveZone(request.getZoneId(), request.getZone());
            if (zone != null) room.setZone(zone);
        }
        if (request.getStatus() != null) room.setStatus(request.getStatus());
        if (request.getOzoneStatus() != null) room.setOzoneStatus(request.getOzoneStatus());

        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(Long roomTypeId, RoomStatus status, String zone) {
        List<Room> list = roomRepository.findAllByOrderByRoomNumberAsc();
        return list.stream()
                .filter(r -> roomTypeId == null || (r.getRoomType() != null && r.getRoomType().getId().equals(roomTypeId)))
                .filter(r -> status == null || r.getStatus() == status)
                .filter(r -> zone == null || zone.isBlank() || zone.equalsIgnoreCase("ALL") || (r.getZone() != null && r.getZone().getName() != null && r.getZone().getName().toLowerCase().contains(zone.toLowerCase())))
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByRoomTypeId(Long roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId).stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomResponse updateRoomStatus(Long id, RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phòng"));
        
        room.setStatus(status);
        Room updated = roomRepository.save(room);
        return RoomResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy phòng");
        }
        roomRepository.deleteById(id);
    }

    private Zone resolveZone(Long zoneId, String zoneName) {
        if (zoneId != null) {
            return zoneRepository.findById(zoneId).orElse(null);
        }
        if (zoneName != null && !zoneName.trim().isEmpty()) {
            String name = zoneName.trim();
            return zoneRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> zoneRepository.save(Zone.builder()
                            .name(name)
                            .matchKey(name.toLowerCase())
                            .tag(name.toUpperCase())
                            .icon("holiday_village")
                            .badgeClass("bg-sky-50 text-sky-700 border-sky-200")
                            .build()));
        }
        return null;
    }
}
