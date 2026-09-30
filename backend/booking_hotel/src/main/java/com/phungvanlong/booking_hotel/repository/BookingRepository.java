package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.Villa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 1. Tìm các căn Villa trống theo Hạng Villa và khoảng ngày
    @Query("SELECT v FROM Villa v WHERE v.villaType.id = :villaTypeId " +
           "AND v.status = 'AVAILABLE' " +
           "AND v.id NOT IN (" +
           "    SELECT bd.villa.id FROM BookingDetail bd JOIN bd.booking b " +
           "    WHERE bd.villa IS NOT NULL AND b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
           "    AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
           ")")
    List<Villa> findAvailableVillas(
            @Param("villaTypeId") Long villaTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    // 2. Tìm các phòng trống (backward compatibility)
    @Query("SELECT r FROM Room r WHERE r.roomType.id = :roomTypeId " +
           "AND r.status = 'AVAILABLE' " +
           "AND r.id NOT IN (" +
           "    SELECT bd.room.id FROM BookingDetail bd JOIN bd.booking b " +
           "    WHERE bd.room IS NOT NULL AND b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
           "    AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
           ")")
    List<Room> findAvailableRooms(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    List<Booking> findByStatusAndExpireAtBefore(BookingStatus status, LocalDateTime now);
    
    List<Booking> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "bookingDetails.villa.villaType"})
    @Query("SELECT b FROM Booking b ORDER BY b.checkInDate DESC")
    Page<Booking> findAllWithDetails(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "bookingDetails.villa.villaType"})
    @Query("SELECT b FROM Booking b WHERE " +
           "(:search IS NULL OR LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(b.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(b.user.phone) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:fromDate IS NULL OR b.checkInDate >= :fromDate) " +
           "AND (:toDate IS NULL OR b.checkOutDate <= :toDate)")
    Page<Booking> searchAdminBookings(
            @Param("search") String search,
            @Param("status") BookingStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);

    long countByStatus(BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.checkInDate = :today AND b.status IN ('CONFIRMED', 'PENDING')")
    long countArrivalsToday(@Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.checkOutDate = :today AND b.status = 'CHECKED_IN'")
    long countDeparturesToday(@Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CHECKED_IN'")
    long countInHouse();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') AND b.createdAt >= :startOfDay")
    BigDecimal calculateTodayRevenue(@Param("startOfDay") LocalDateTime startOfDay);
}
