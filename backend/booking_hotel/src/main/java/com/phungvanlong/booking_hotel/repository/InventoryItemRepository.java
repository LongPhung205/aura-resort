package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByCode(String code);

    boolean existsByCode(String code);

    List<InventoryItem> findByCategory(InventoryCategory category);

    @Query("SELECT i FROM InventoryItem i WHERE " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY i.name ASC")
    List<InventoryItem> searchItems(@Param("category") InventoryCategory category, @Param("keyword") String keyword);

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.inStock <= i.minThreshold")
    long countLowStockItems();

    @Query("SELECT COALESCE(SUM(CAST(i.inStock AS bigdecimal) * i.unitPrice), 0) FROM InventoryItem i")
    BigDecimal calculateTotalInventoryValue();
}
