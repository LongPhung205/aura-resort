package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.ExtraService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraServiceRepository extends JpaRepository<ExtraService, Long> {
}
