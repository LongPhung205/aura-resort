package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillaRepository extends JpaRepository<Villa, Long> {

    Optional<Villa> findByVillaNumber(String villaNumber);
    Optional<Villa> findByVillaNumberAndZoneId(String villaNumber, Long zoneId);
    List<Villa> findByVillaTypeId(Long villaTypeId);
    
    List<Villa> findByStatus(VillaStatus status);

    List<Villa> findByZoneId(Long zoneId);

    long countByZoneId(Long zoneId);

    long countByStatus(VillaStatus status);

    @EntityGraph(attributePaths = {"villaType", "zone", "images", "rooms.roomType"})
    @Query("SELECT v FROM Villa v WHERE " +
           "(:typeId IS NULL OR v.villaType.id = :typeId) AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:zone IS NULL OR (v.zone IS NOT NULL AND v.zone.name = :zone))")
    List<Villa> findByFilters(
            @Param("typeId") Long typeId,
            @Param("status") VillaStatus status,
            @Param("zone") String zone);

    // Dùng cho Gantt chart — chỉ cần villaType + zone, không cần images/rooms
    @EntityGraph(attributePaths = {"villaType", "zone"})
    @Query("SELECT v FROM Villa v")
    List<Villa> findAllWithRelations();

    @EntityGraph(attributePaths = {"villaType", "zone", "images", "rooms.roomType", "villaServices.service"})
    @Query("SELECT v FROM Villa v WHERE v.id = :id")
    Optional<Villa> findByIdWithFullRelations(@Param("id") Long id);

    @EntityGraph(attributePaths = {"villaType", "zone", "images"})
    @Query("SELECT v FROM Villa v WHERE v.status != 'MAINTENANCE' " +
           "AND (:zoneName IS NULL OR :zoneName = 'all' OR LOWER(v.zone.name) LIKE LOWER(CONCAT('%', :zoneName, '%'))) " +
           "AND (:minAdults IS NULL OR COALESCE((SELECT SUM(r.roomType.capacity) FROM Room r WHERE r.villa = v), v.villaType.capacity) >= :minAdults) " +
           "AND v.id NOT IN (" +
           "    SELECT bd.villa.id FROM BookingDetail bd JOIN bd.booking b " +
           "    WHERE bd.villa IS NOT NULL AND (b.status IN ('CONFIRMED', 'CHECKED_IN') OR (b.status = 'PENDING' AND (b.expireAt IS NULL OR b.expireAt > :now))) " +
           "    AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
           ")")
    List<Villa> searchAvailableVillas(
            @Param("zoneName") String zoneName,
            @Param("minAdults") Integer minAdults,
            @Param("checkInDate") java.time.LocalDate checkInDate,
            @Param("checkOutDate") java.time.LocalDate checkOutDate,
            @Param("now") java.time.LocalDateTime now);

    default List<Villa> searchAvailableVillas(
            String zoneName,
            Integer minAdults,
            java.time.LocalDate checkInDate,
            java.time.LocalDate checkOutDate) {
        return searchAvailableVillas(zoneName, minAdults, checkInDate, checkOutDate, java.time.LocalDateTime.now());
    }
}

