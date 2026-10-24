package com.phungvanlong.booking_hotel.repository.specification;

import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filter(
            String tab,
            Role role,
            Boolean isActive,
            String search
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Phân loại theo Tab (CUSTOMER vs STAFF)
            if ("CUSTOMER".equalsIgnoreCase(tab)) {
                predicates.add(cb.equal(root.get("role"), Role.ROLE_CUSTOMER));
            } else if ("STAFF".equalsIgnoreCase(tab)) {
                predicates.add(cb.notEqual(root.get("role"), Role.ROLE_CUSTOMER));
            }

            // 2. Lọc theo Role cụ thể
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            // 3. Lọc theo trạng thái kích hoạt (isActive)
            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            // 4. Tìm kiếm từ khóa (Họ tên, Email, Số điện thoại)
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), pattern);
                Predicate phoneMatch = cb.and(cb.isNotNull(root.get("phone")), cb.like(root.get("phone"), pattern));
                predicates.add(cb.or(nameMatch, emailMatch, phoneMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
