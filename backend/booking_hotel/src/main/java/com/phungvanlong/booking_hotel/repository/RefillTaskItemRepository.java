package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.RefillTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefillTaskItemRepository extends JpaRepository<RefillTaskItem, Long> {

    List<RefillTaskItem> findByRefillTaskId(Long refillTaskId);
}
