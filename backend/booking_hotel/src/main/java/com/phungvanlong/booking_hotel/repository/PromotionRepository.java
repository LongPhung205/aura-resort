package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);

    @Query("SELECT p FROM Promotion p WHERE p.startDate <= :today AND p.endDate >= :today ORDER BY p.id DESC")
    List<Promotion> findActivePromotions(LocalDate today);
}
