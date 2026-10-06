package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Payment;
import com.phungvanlong.booking_hotel.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    boolean existsByBookingIdAndStatus(Long bookingId, PaymentStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p FROM Payment p " +
           "LEFT JOIN FETCH p.booking b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.bookingDetails bd " +
           "LEFT JOIN FETCH bd.villa " +
           "LEFT JOIN FETCH bd.room " +
           "ORDER BY p.id DESC")
    java.util.List<Payment> findAllWithBookingDetails();
}
