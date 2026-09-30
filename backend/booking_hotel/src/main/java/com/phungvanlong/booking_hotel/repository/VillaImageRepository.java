package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.VillaImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillaImageRepository extends JpaRepository<VillaImage, Long> {

    List<VillaImage> findByVillaId(Long villaId);

    void deleteByVillaId(Long villaId);
}
