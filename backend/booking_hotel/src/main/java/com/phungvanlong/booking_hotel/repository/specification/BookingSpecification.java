package com.phungvanlong.booking_hotel.repository.specification;

import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import com.phungvanlong.booking_hotel.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingSpecification {

    public static Specification<Booking> filter(
            String search,
            BookingStatus status,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm từ khóa đa trường (Mã đơn, Khách vãng lai, Tài khoản người dùng)
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Join<Booking, User> userJoin = root.join("user", JoinType.LEFT);

                Predicate bookingCodeMatch = cb.like(cb.lower(root.get("bookingCode")), pattern);
                Predicate guestNameMatch = cb.and(cb.isNotNull(root.get("guestName")), cb.like(cb.lower(root.get("guestName")), pattern));
                Predicate guestPhoneMatch = cb.and(cb.isNotNull(root.get("guestPhone")), cb.like(root.get("guestPhone"), pattern));
                Predicate guestEmailMatch = cb.and(cb.isNotNull(root.get("guestEmail")), cb.like(cb.lower(root.get("guestEmail")), pattern));
                Predicate userFullNameMatch = cb.and(cb.isNotNull(userJoin.get("fullName")), cb.like(cb.lower(userJoin.get("fullName")), pattern));
                Predicate userPhoneMatch = cb.and(cb.isNotNull(userJoin.get("phone")), cb.like(userJoin.get("phone"), pattern));
                Predicate userEmailMatch = cb.and(cb.isNotNull(userJoin.get("email")), cb.like(cb.lower(userJoin.get("email")), pattern));

                predicates.add(cb.or(
                        bookingCodeMatch,
                        guestNameMatch,
                        guestPhoneMatch,
                        guestEmailMatch,
                        userFullNameMatch,
                        userPhoneMatch,
                        userEmailMatch
                ));
            }

            // 2. Lọc theo trạng thái đơn
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 3. Lọc theo khoảng ngày check-in / check-out
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("checkInDate"), fromDate));
            }

            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("checkOutDate"), toDate));
            }

            // Tránh bản ghi trùng lặp khi có join và chỉ áp dụng cho truy vấn dữ liệu (không count)
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
