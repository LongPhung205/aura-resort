package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.dto.response.HousekeeperSummaryDto;
import com.phungvanlong.booking_hotel.entity.HousekeepingTask;
import com.phungvanlong.booking_hotel.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {

    @Override
    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    List<HousekeepingTask> findAll();

    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    List<HousekeepingTask> findByVillaIdOrderByCreatedAtDesc(Long villaId);

    Optional<HousekeepingTask> findFirstByVillaIdAndStatusIn(Long villaId, List<String> statuses);

    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    List<HousekeepingTask> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    Optional<HousekeepingTask> findFirstByRoomIdAndStatusIn(Long roomId, List<String> statuses);

    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    List<HousekeepingTask> findByStatusOrderByCreatedAtDesc(String status);

    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    List<HousekeepingTask> findByStatusInOrderByCreatedAtDesc(List<String> statuses);

    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    List<HousekeepingTask> findByHousekeeperIdOrderByCreatedAtDesc(Long housekeeperId);

    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    List<HousekeepingTask> findByHousekeeperEmailOrderByCreatedAtDesc(String email);
    
    @EntityGraph(attributePaths = {"villa", "villa.villaType", "villa.zone", "room", "room.roomType", "housekeeper", "supervisor", "booking"})
    @Query("SELECT t FROM HousekeepingTask t WHERE UPPER(t.status) = 'PENDING' OR t.housekeeper IS NULL ORDER BY t.createdAt DESC")
    List<HousekeepingTask> findAvailableDirtyTasks();

    @Query("SELECT new com.phungvanlong.booking_hotel.dto.response.HousekeeperSummaryDto(" +
           "u.id, u.fullName, u.email, u.phone, COUNT(t.id)) " +
           "FROM User u " +
           "LEFT JOIN HousekeepingTask t ON t.housekeeper = u AND UPPER(t.status) NOT IN ('COMPLETED', 'CANCELLED') " +
           "WHERE u.role = :role " +
           "GROUP BY u.id, u.fullName, u.email, u.phone")
    List<HousekeeperSummaryDto> findHousekeeperSummariesWithActiveTaskCount(@Param("role") Role role);

    boolean existsByVillaIdAndTaskTypeAndCreatedAtAfter(Long villaId, String taskType, java.time.LocalDateTime createdAt);
}
