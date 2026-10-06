package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {
    List<ShiftSwapRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<ShiftSwapRequest> findAllByOrderByCreatedAtDesc();

    @Query("SELECT r FROM ShiftSwapRequest r " +
           "LEFT JOIN FETCH r.requester " +
           "LEFT JOIN FETCH r.targetStaff " +
           "LEFT JOIN FETCH r.currentShift " +
           "LEFT JOIN FETCH r.desiredShift " +
           "LEFT JOIN FETCH r.approver " +
           "ORDER BY r.createdAt DESC")
    List<ShiftSwapRequest> findAllWithDetails();
}
