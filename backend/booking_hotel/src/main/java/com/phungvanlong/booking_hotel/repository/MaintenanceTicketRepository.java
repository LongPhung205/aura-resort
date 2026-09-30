package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.MaintenanceStatus;
import com.phungvanlong.booking_hotel.entity.MaintenanceTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket, Long> {
    Optional<MaintenanceTicket> findByTicketCode(String ticketCode);
    List<MaintenanceTicket> findByVillaId(Long villaId);
    List<MaintenanceTicket> findByStatus(MaintenanceStatus status);
}
