package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {
    List<ShiftSwapRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<ShiftSwapRequest> findAllByOrderByCreatedAtDesc();
}
