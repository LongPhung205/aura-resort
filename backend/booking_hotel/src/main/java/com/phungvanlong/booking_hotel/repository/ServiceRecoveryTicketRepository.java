package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.ServiceRecoveryTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRecoveryTicketRepository extends JpaRepository<ServiceRecoveryTicket, Long> {
    List<ServiceRecoveryTicket> findByStatusOrderByCreatedAtDesc(String status);
    List<ServiceRecoveryTicket> findAllByOrderByCreatedAtDesc();
}
