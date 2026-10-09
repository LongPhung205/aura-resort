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
           "AND v.status != 'MAINTENANCE' " +
           "AND v.id NOT IN (" +
           "    SELECT bd.villa.id FROM BookingDetail bd JOIN bd.booking b " +
           "    WHERE bd.villa IS NOT NULL AND (b.status IN ('CONFIRMED', 'CHECKED_IN') OR (b.status = 'PENDING' AND (b.expireAt IS NULL OR b.expireAt > :now))) " +
           "    AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
           ")")
    List<Villa> findAvailableVillas(
            @Param("villaTypeId") Long villaTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("now") LocalDateTime now);

    default List<Villa> findAvailableVillas(Long villaTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        return findAvailableVillas(villaTypeId, checkInDate, checkOutDate, LocalDateTime.now());
    }

    // 2. Kiểm tra số đơn đặt phòng trùng lịch cho 1 căn Villa cụ thể
    @Query("SELECT COUNT(bd) FROM BookingDetail bd JOIN bd.booking b " +
           "WHERE bd.villa.id = :villaId " +
           "AND (b.status IN ('CONFIRMED', 'CHECKED_IN') OR (b.status = 'PENDING' AND (b.expireAt IS NULL OR b.expireAt > :now))) " +
           "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    long countOverlappingBookingsForVilla(
            @Param("villaId") Long villaId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("now") LocalDateTime now);

    default long countOverlappingBookingsForVilla(Long villaId, LocalDate checkInDate, LocalDate checkOutDate) {
        return countOverlappingBookingsForVilla(villaId, checkInDate, checkOutDate, LocalDateTime.now());
    }

    // 3. Tìm các phòng trống (backward compatibility)
    @Query("SELECT r FROM Room r WHERE r.roomType.id = :roomTypeId " +
           "AND r.status != 'MAINTENANCE' " +
           "AND r.id NOT IN (" +
           "    SELECT bd.room.id FROM BookingDetail bd JOIN bd.booking b " +
           "    WHERE bd.room IS NOT NULL AND (b.status IN ('CONFIRMED', 'CHECKED_IN') OR (b.status = 'PENDING' AND (b.expireAt IS NULL OR b.expireAt > :now))) " +
           "    AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
           ")")
    List<Room> findAvailableRooms(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("now") LocalDateTime now);

    default List<Room> findAvailableRooms(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        return findAvailableRooms(roomTypeId, checkInDate, checkOutDate, LocalDateTime.now());
    }

    List<Booking> findByStatusAndExpireAtBefore(BookingStatus status, LocalDateTime now);

    @EntityGraph(attributePaths = {"user", "bookingDetails.villa", "bookingDetails.room"})
    List<Booking> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "bookingDetails.villa.villaType"})
    @Query("SELECT b FROM Booking b ORDER BY b.checkInDate DESC")
    Page<Booking> findAllWithDetails(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "bookingDetails.villa.villaType"})
    @Query("SELECT b FROM Booking b LEFT JOIN b.user u WHERE " +
           "(:search IS NULL OR LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR (u IS NOT NULL AND LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           " OR (u IS NOT NULL AND LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           " OR (b.guestName IS NOT NULL AND LOWER(b.guestName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           " OR (b.guestPhone IS NOT NULL AND LOWER(b.guestPhone) LIKE LOWER(CONCAT('%', :search, '%')))) " +
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

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.checkInDate = :today AND b.status = 'CONFIRMED'")
    long countArrivalsToday(@Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.checkOutDate = :today AND b.status = 'CHECKED_IN'")
    long countDeparturesToday(@Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CHECKED_IN'")
    long countInHouse();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') AND b.createdAt >= :startOfDay")
    BigDecimal calculateTodayRevenue(@Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') " +
           "AND b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findActiveBookingsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND (b.checkInDate = :today OR b.checkOutDate = :today)")
    List<Booking> findArrivalDepartureToday(@Param("today") LocalDate today);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') AND b.createdAt >= :startOfDay AND b.createdAt < :endOfDay")
    BigDecimal calculateRevenueByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
