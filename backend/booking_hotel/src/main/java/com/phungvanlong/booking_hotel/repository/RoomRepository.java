package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByVillaId(Long villaId);

    List<Room> findByVillaIdOrderByRoomNumberAsc(Long villaId);

    @EntityGraph(attributePaths = {"roomType"})
    List<Room> findByRoomTypeId(Long roomTypeId);

    boolean existsByRoomNumber(String roomNumber);

    Optional<Room> findByRoomNumber(String roomNumber);

    @EntityGraph(attributePaths = {"roomType"})
    List<Room> findAllByOrderByRoomNumberAsc();
}
