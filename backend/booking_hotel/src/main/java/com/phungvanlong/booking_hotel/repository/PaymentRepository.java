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
}
