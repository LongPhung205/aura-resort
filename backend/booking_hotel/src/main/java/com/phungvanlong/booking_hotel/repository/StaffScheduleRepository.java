package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, Long> {
    List<StaffSchedule> findByWorkDateBetweenOrderByWorkDateAsc(LocalDate startDate, LocalDate endDate);
    List<StaffSchedule> findByStaffIdAndWorkDateBetween(Long staffId, LocalDate startDate, LocalDate endDate);
    java.util.Optional<StaffSchedule> findByStaffIdAndWorkDate(Long staffId, LocalDate workDate);
    void deleteByWorkDateBetween(LocalDate startDate, LocalDate endDate);
}
