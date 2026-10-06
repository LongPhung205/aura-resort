package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    List<HousekeepingTask> findByVillaIdOrderByCreatedAtDesc(Long villaId);
    Optional<HousekeepingTask> findFirstByVillaIdAndStatusIn(Long villaId, List<String> statuses);

    List<HousekeepingTask> findByRoomIdOrderByCreatedAtDesc(Long roomId);
    Optional<HousekeepingTask> findFirstByRoomIdAndStatusIn(Long roomId, List<String> statuses);

    List<HousekeepingTask> findByStatusOrderByCreatedAtDesc(String status);
    List<HousekeepingTask> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
    List<HousekeepingTask> findByHousekeeperIdOrderByCreatedAtDesc(Long housekeeperId);
    List<HousekeepingTask> findByHousekeeperEmailOrderByCreatedAtDesc(String email);
    
    boolean existsByVillaIdAndTaskTypeAndCreatedAtAfter(Long villaId, String taskType, java.time.LocalDateTime createdAt);
}
