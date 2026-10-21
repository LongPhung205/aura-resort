package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id IN :ids")
    List<Room> findAllByIdForUpdate(@Param("ids") List<Long> ids);
    
    List<Room> findByVillaId(Long villaId);

    List<Room> findByVillaIdOrderByRoomNumberAsc(Long villaId);

    @EntityGraph(attributePaths = {"roomType"})
    List<Room> findByRoomTypeId(Long roomTypeId);

    boolean existsByRoomNumber(String roomNumber);

    Optional<Room> findByRoomNumber(String roomNumber);

    @EntityGraph(attributePaths = {"roomType"})
    List<Room> findAllByOrderByRoomNumberAsc();
}
