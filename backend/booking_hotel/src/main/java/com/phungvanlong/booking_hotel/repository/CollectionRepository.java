package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByIsActiveTrueOrderByDisplayOrderAsc();
    Optional<Collection> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
