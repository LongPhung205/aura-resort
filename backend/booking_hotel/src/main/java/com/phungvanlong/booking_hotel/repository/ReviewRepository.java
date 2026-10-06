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

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT r FROM Review r LEFT JOIN FETCH r.booking b LEFT JOIN FETCH b.user u LEFT JOIN FETCH r.villaType vt LEFT JOIN FETCH r.roomType rt ORDER BY r.createdAt DESC")
    List<Review> findAllWithDetails();

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Review r LEFT JOIN FETCH r.booking b LEFT JOIN FETCH b.user u LEFT JOIN FETCH r.villaType vt LEFT JOIN FETCH r.roomType rt WHERE r.booking.user.email = :email ORDER BY r.createdAt DESC")
    List<Review> findByUserEmail(@org.springframework.data.repository.query.Param("email") String email);
}
