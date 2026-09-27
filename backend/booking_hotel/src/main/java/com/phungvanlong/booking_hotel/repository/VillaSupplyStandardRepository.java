package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.VillaSupplyStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillaSupplyStandardRepository extends JpaRepository<VillaSupplyStandard, Long> {

    List<VillaSupplyStandard> findByVillaIdOrderByItemNameAsc(Long villaId);

    List<VillaSupplyStandard> findByVillaId(Long villaId);

    Optional<VillaSupplyStandard> findByVillaIdAndItemId(Long villaId, Long itemId);

    boolean existsByVillaIdAndItemId(Long villaId, Long itemId);
}
