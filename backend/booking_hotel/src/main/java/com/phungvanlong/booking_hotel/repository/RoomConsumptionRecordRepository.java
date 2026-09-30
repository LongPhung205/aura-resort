package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.RoomConsumptionRecord;
import com.phungvanlong.booking_hotel.entity.RoomConsumptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomConsumptionRecordRepository extends JpaRepository<RoomConsumptionRecord, Long> {
    List<RoomConsumptionRecord> findByHousekeepingTaskId(Long housekeepingTaskId);
    List<RoomConsumptionRecord> findByBookingId(Long bookingId);
    List<RoomConsumptionRecord> findByBookingIdAndStatus(Long bookingId, RoomConsumptionStatus status);
    List<RoomConsumptionRecord> findByVillaId(Long villaId);
    List<RoomConsumptionRecord> findByStatus(RoomConsumptionStatus status);
}
