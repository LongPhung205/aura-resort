package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.VillaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VillaTypeRepository extends JpaRepository<VillaType, Long> {

    Optional<VillaType> findByName(String name);

    List<VillaType> findByNameContainingIgnoreCase(String keyword);

    boolean existsByName(String name);

    @Query("SELECT vt FROM VillaType vt WHERE " +
           "(:capacity IS NULL OR vt.capacity >= :capacity) AND " +
           "(:minPrice IS NULL OR vt.basePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR vt.basePrice <= :maxPrice) AND " +
           "(:quantity <= (" +
           "   SELECT count(v.id) FROM Villa v WHERE v.villaType = vt AND v.status = 'AVAILABLE' AND v.id NOT IN (" +
           "       SELECT bd.villa.id FROM BookingDetail bd JOIN bd.booking b " +
           "       WHERE bd.villa IS NOT NULL AND b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
           "       AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
           "   )" +
           "))")
    Page<VillaType> searchVillaTypes(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("quantity") Integer quantity,
            @Param("capacity") Integer capacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
