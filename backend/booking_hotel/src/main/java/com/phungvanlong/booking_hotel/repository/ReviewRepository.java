package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVillaTypeId(Long villaTypeId);
    List<Review> findByRoomTypeId(Long roomTypeId);
    boolean existsByBookingId(Long bookingId);
}
