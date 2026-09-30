package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    List<AttendanceLog> findByCheckTimeBetweenOrderByCheckTimeDesc(LocalDateTime start, LocalDateTime end);
    List<AttendanceLog> findTop20ByOrderByCheckTimeDesc();
}
