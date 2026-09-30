package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
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

    @Query("SELECT v FROM Villa v WHERE " +
           "(:typeId IS NULL OR v.villaType.id = :typeId) AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:zone IS NULL OR (v.zone IS NOT NULL AND v.zone.name = :zone))")
    List<Villa> findByFilters(
            @Param("typeId") Long typeId,
            @Param("status") VillaStatus status,
            @Param("zone") String zone);
}
