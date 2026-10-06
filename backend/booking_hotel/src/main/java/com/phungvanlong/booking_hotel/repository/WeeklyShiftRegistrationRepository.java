package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.WeeklyShiftRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyShiftRegistrationRepository extends JpaRepository<WeeklyShiftRegistration, Long> {

    @Query("SELECT DISTINCT r FROM WeeklyShiftRegistration r " +
           "LEFT JOIN FETCH r.staff " +
           "LEFT JOIN FETCH r.details " +
           "WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<WeeklyShiftRegistration> findByStatusWithDetails(String status);

    @Query("SELECT DISTINCT r FROM WeeklyShiftRegistration r " +
           "LEFT JOIN FETCH r.staff " +
           "LEFT JOIN FETCH r.details " +
           "ORDER BY r.createdAt DESC")
    List<WeeklyShiftRegistration> findAllWithDetails();

    @Query("SELECT r FROM WeeklyShiftRegistration r " +
           "LEFT JOIN FETCH r.details " +
           "WHERE r.staff.id = :staffId AND r.weekStartDate = :weekStartDate")
    Optional<WeeklyShiftRegistration> findByStaffIdAndWeekStartDate(Long staffId, LocalDate weekStartDate);

    List<WeeklyShiftRegistration> findByStaffIdOrderByCreatedAtDesc(Long staffId);
}
