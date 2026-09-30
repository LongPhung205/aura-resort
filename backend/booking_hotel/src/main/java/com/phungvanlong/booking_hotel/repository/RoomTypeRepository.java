package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    
    java.util.Optional<RoomType> findByName(String name);
    
    @Query("SELECT rt FROM RoomType rt WHERE " +
           "(:capacity IS NULL OR rt.capacity >= :capacity) AND " +
           "(:minPrice IS NULL OR rt.basePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR rt.basePrice <= :maxPrice) AND " +
           "(:quantity <= (" +
           "   SELECT count(r.id) FROM Room r WHERE r.roomType = rt AND r.status = 'AVAILABLE' AND r.id NOT IN (" +
           "       SELECT bd.room.id FROM BookingDetail bd JOIN bd.booking b " +
           "       WHERE b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
           "       AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
           "   )" +
           "))")
    Page<RoomType> searchRoomTypes(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("quantity") Integer quantity,
            @Param("capacity") Integer capacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
