package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.ComboPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboPackageRepository extends JpaRepository<ComboPackage, Long> {
    List<ComboPackage> findByStatusOrderByCreatedAtDesc(String status);
}
