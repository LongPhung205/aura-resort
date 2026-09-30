package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.YieldRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YieldRuleRepository extends JpaRepository<YieldRule, Long> {
    List<YieldRule> findByIsActiveTrue();
}
