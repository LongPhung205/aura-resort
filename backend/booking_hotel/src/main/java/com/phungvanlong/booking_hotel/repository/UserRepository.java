package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByRole(Role role);

    long countByRole(Role role);

    long countByRoleNot(Role role);

    long countByIsActiveFalse();

    @Query("SELECT u FROM User u WHERE " +
            "((:tab = 'CUSTOMER' AND u.role = com.phungvanlong.booking_hotel.entity.Role.ROLE_CUSTOMER) OR " +
            " (:tab = 'STAFF' AND u.role != com.phungvanlong.booking_hotel.entity.Role.ROLE_CUSTOMER) OR " +
            " (:tab IS NULL OR :tab = 'ALL')) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
            "(:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " (u.phone IS NOT NULL AND u.phone LIKE CONCAT('%', :search, '%')))")
    Page<User> findByFilters(
            @Param("tab") String tab,
            @Param("role") Role role,
            @Param("isActive") Boolean isActive,
            @Param("search") String search,
            Pageable pageable
    );

    // Aggregation queries — dùng SQL thay vì load toàn bộ bookings vào memory
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.user.id = :userId AND b.status != 'CANCELLED'")
    double sumTotalSpentByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId")
    int countBookingsByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(b.createdAt) FROM Booking b WHERE b.user.id = :userId")
    java.time.LocalDateTime findLastBookingDateByUserId(@Param("userId") Long userId);
}

