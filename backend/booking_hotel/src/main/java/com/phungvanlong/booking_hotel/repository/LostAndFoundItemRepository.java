package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.LostAndFoundItem;
import com.phungvanlong.booking_hotel.entity.LostAndFoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LostAndFoundItemRepository extends JpaRepository<LostAndFoundItem, Long> {
    Optional<LostAndFoundItem> findByItemCode(String itemCode);
    List<LostAndFoundItem> findByVillaId(Long villaId);
    List<LostAndFoundItem> findByStatus(LostAndFoundStatus status);
    List<LostAndFoundItem> findByBookingId(Long bookingId);
}
