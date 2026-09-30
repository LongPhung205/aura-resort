package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.ServiceDispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceDispatchRepository extends JpaRepository<ServiceDispatch, Long> {
    List<ServiceDispatch> findByStatusOrderByScheduledTimeAsc(String status);
    List<ServiceDispatch> findAllByOrderByScheduledTimeDesc();
}
