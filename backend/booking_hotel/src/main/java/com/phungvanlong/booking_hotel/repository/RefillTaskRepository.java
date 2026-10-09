package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.RefillTask;
import com.phungvanlong.booking_hotel.entity.RefillTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefillTaskRepository extends JpaRepository<RefillTask, Long> {

    List<RefillTask> findAllByOrderByCreatedAtDesc();

    List<RefillTask> findByStatusOrderByCreatedAtDesc(RefillTaskStatus status);

    List<RefillTask> findByVillaIdOrderByCreatedAtDesc(Long villaId);

    List<RefillTask> findByVillaIdAndStatusOrderByCreatedAtDesc(Long villaId, RefillTaskStatus status);

    Optional<RefillTask> findByTaskCode(String taskCode);

    List<RefillTask> findByHousekeepingTaskBookingId(Long bookingId);

    long countByStatus(RefillTaskStatus status);
}
