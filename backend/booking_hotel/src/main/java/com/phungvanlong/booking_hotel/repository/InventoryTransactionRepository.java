package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findAllByOrderByCreatedAtDesc();

    List<InventoryTransaction> findTop50ByOrderByCreatedAtDesc();

    List<InventoryTransaction> findByItemIdOrderByCreatedAtDesc(Long itemId);
}
