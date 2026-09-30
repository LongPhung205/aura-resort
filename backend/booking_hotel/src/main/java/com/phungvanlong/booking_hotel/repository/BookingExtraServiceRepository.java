package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.BookingExtraService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingExtraServiceRepository extends JpaRepository<BookingExtraService, Long> {
}
