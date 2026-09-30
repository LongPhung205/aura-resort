package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.VillaService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillaServiceRepository extends JpaRepository<VillaService, Long> {
    List<VillaService> findByVillaId(Long villaId);
    List<VillaService> findByServiceId(Long serviceId);
    Optional<VillaService> findByVillaIdAndServiceId(Long villaId, Long serviceId);
    void deleteByVillaIdAndServiceId(Long villaId, Long serviceId);
    int countByServiceId(Long serviceId);
    int countByServiceIdAndIsAvailableTrue(Long serviceId);
}
