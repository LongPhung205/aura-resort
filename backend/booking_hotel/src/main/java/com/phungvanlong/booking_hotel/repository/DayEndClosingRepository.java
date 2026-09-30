package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.DayEndClosing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DayEndClosingRepository extends JpaRepository<DayEndClosing, Long> {
    Optional<DayEndClosing> findByClosingDate(LocalDate closingDate);
    Optional<DayEndClosing> findTopByOrderByClosingDateDesc();
}
