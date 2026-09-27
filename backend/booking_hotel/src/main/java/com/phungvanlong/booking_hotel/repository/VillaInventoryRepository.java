package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.VillaInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillaInventoryRepository extends JpaRepository<VillaInventory, Long> {

    List<VillaInventory> findByVillaIdOrderByItemNameAsc(Long villaId);

    List<VillaInventory> findByVillaId(Long villaId);

    Optional<VillaInventory> findByVillaIdAndItemId(Long villaId, Long itemId);
}
