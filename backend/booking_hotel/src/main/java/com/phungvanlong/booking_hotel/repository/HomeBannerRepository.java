package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.HomeBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeBannerRepository extends JpaRepository<HomeBanner, Long> {
    List<HomeBanner> findAllByIsActiveTrueOrderByDisplayOrderAsc();
    List<HomeBanner> findAllByOrderByDisplayOrderAsc();
}
