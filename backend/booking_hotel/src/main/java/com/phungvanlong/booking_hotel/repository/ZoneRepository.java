package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByNameIgnoreCase(String name);

    Optional<Zone> findBySlugIgnoreCase(String slug);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);

    List<Zone> findAllByOrderByCreatedAtAsc();

    List<Zone> findAllByOrderByDisplayOrderAsc();

    List<Zone> findAllByIsActiveTrueOrderByDisplayOrderAsc();
}
