package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.VillaAsset;
import com.phungvanlong.booking_hotel.entity.VillaAssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillaAssetRepository extends JpaRepository<VillaAsset, Long> {

    List<VillaAsset> findAllByOrderByCreatedAtDesc();

    List<VillaAsset> findByVillaIdOrderByCreatedAtDesc(Long villaId);

    List<VillaAsset> findByStatus(VillaAssetStatus status);
}
