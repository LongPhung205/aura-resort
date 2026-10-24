package com.phungvanlong.booking_hotel.repository.specification;

import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VillaSpecification {

    public static Specification<Villa> filter(Long typeId, VillaStatus status, String zone) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo Loại Villa
            if (typeId != null) {
                predicates.add(cb.equal(root.get("villaType").get("id"), typeId));
            }

            // 2. Lọc theo Trạng thái vận hành
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 3. Lọc theo Phân khu (Zone)
            if (zone != null && !zone.isBlank() && !"ALL".equalsIgnoreCase(zone)) {
                predicates.add(cb.equal(root.get("zone").get("name"), zone.trim()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
