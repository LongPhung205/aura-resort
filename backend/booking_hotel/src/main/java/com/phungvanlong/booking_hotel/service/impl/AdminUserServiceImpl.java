package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.CreateUserAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.ResetPasswordAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateUserAdminRequest;
import com.phungvanlong.booking_hotel.dto.request.UserFilterRequest;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.UserResponseDto;
import com.phungvanlong.booking_hotel.dto.response.UserSummaryStatsDto;
import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.BookingRepository;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import com.phungvanlong.booking_hotel.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponseDto> getUsers(UserFilterRequest filter) {
        int page = Math.max(0, filter.getPage());
        int size = filter.getSize() <= 0 ? 10 : filter.getSize();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        String tab = filter.getTab();
        if (tab == null || tab.trim().isEmpty()) {
            tab = "CUSTOMER";
        }

        String search = (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) 
                ? filter.getSearch().trim() : null;

        Page<User> userPage = userRepository.findByFilters(
                tab,
                filter.getRole(),
                filter.getIsActive(),
                search,
                pageable
        );

        Page<UserResponseDto> dtoPage = userPage.map(this::mapToDto);
        return PageResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryStatsDto getUserStats() {
        long totalUsers = userRepository.count();
        long totalCustomers = userRepository.countByRole(Role.ROLE_CUSTOMER);
        long totalStaff = userRepository.countByRoleNot(Role.ROLE_CUSTOMER);
        long totalLocked = userRepository.countByIsActiveFalse();

        return UserSummaryStatsDto.builder()
                .totalUsers(totalUsers)
                .totalCustomers(totalCustomers)
                .totalStaff(totalStaff)
                .totalLocked(totalLocked)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với mã ID: " + id));
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserAdminRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email '" + request.getEmail() + "' đã được sử dụng trong hệ thống.");
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty() && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Số điện thoại '" + request.getPhone() + "' đã được sử dụng.");
        }

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .role(request.getRole())
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Admin created new user ID={} with email={} and role={}", saved.getId(), saved.getEmail(), saved.getRole());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UpdateUserAdminRequest request, String currentAdminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        if (user.getEmail().equalsIgnoreCase(currentAdminEmail) && request.getRole() != user.getRole()) {
            throw new BusinessException("Không thể tự thay đổi vai trò của tài khoản Admin đang đăng nhập.");
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (!request.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("Số điện thoại '" + request.getPhone() + "' đã được sử dụng bởi tài khoản khác.");
            }
            user.setPhone(request.getPhone().trim());
        } else {
            user.setPhone(null);
        }

        user.setFullName(request.getFullName().trim());
        user.setRole(request.getRole());

        User updated = userRepository.save(user);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public UserResponseDto toggleUserStatus(Long id, String currentAdminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        if (user.getEmail().equalsIgnoreCase(currentAdminEmail)) {
            throw new BusinessException("Không thể tự khóa tài khoản của chính mình.");
        }

        boolean currentStatus = Boolean.TRUE.equals(user.getIsActive());
        user.setIsActive(!currentStatus);
        User updated = userRepository.save(user);
        log.info("Admin toggled user ID={} status to isActive={}", updated.getId(), updated.getIsActive());
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, ResetPasswordAdminRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Admin reset password for user ID={}", user.getId());
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String currentAdminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        if (user.getEmail().equalsIgnoreCase(currentAdminEmail)) {
            throw new BusinessException("Không thể tự xóa tài khoản của chính mình.");
        }

        // 1. Kiểm tra các đơn đặt phòng của người dùng
        List<Booking> userBookings = bookingRepository.findByUserId(id);
        boolean hasActiveBookings = userBookings.stream().anyMatch(b ->
                b.getStatus() == BookingStatus.PENDING ||
                b.getStatus() == BookingStatus.CONFIRMED ||
                b.getStatus() == BookingStatus.CHECKED_IN
        );

        if (hasActiveBookings) {
            throw new BusinessException("Không thể xóa tài khoản đang có đơn đặt phòng đang hoạt động hoặc đang lưu trú. Vui lòng xử lý hoặc hủy các đơn đặt phòng này trước.");
        }

        // 2. Bảo lưu lịch sử hóa đơn / đặt phòng cũ (CHECKED_OUT, CANCELLED)
        if (!userBookings.isEmpty()) {
            try {
                jdbcTemplate.execute("ALTER TABLE bookings MODIFY COLUMN user_id BIGINT NULL");
            } catch (Exception e) {
                log.warn("Could not modify bookings.user_id nullability: {}", e.getMessage());
            }

            for (Booking b : userBookings) {
                if (b.getGuestName() == null || b.getGuestName().trim().isEmpty()) {
                    b.setGuestName(user.getFullName());
                }
                if (b.getGuestEmail() == null || b.getGuestEmail().trim().isEmpty()) {
                    b.setGuestEmail(user.getEmail());
                }
                if (b.getGuestPhone() == null || b.getGuestPhone().trim().isEmpty()) {
                    b.setGuestPhone(user.getPhone());
                }
                b.setUser(null);
                bookingRepository.save(b);
            }
        }

        // 3. Tháo gỡ các liên kết nhân viên nếu có (Housekeeping, Dispatch, Ticket...)
        try {
            jdbcTemplate.execute("UPDATE housekeeping_tasks SET housekeeper_id = NULL WHERE housekeeper_id = " + id);
            jdbcTemplate.execute("UPDATE housekeeping_tasks SET supervisor_id = NULL WHERE supervisor_id = " + id);
            jdbcTemplate.execute("UPDATE service_recovery_tickets SET assigned_manager_id = NULL WHERE assigned_manager_id = " + id);
            jdbcTemplate.execute("UPDATE service_dispatches SET assigned_staff_id = NULL WHERE assigned_staff_id = " + id);
            jdbcTemplate.execute("UPDATE weekly_shift_registrations SET approver_id = NULL WHERE approver_id = " + id);
            jdbcTemplate.execute("UPDATE shift_swap_requests SET approver_id = NULL WHERE approver_id = " + id);
        } catch (Exception e) {
            log.warn("Non-critical cleanup for staff references: {}", e.getMessage());
        }

        // 4. Xóa tài khoản
        try {
            userRepository.delete(user);
            log.info("Admin [{}] deleted user ID={} ({}) successfully", currentAdminEmail, id, user.getEmail());
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to delete user ID={} due to integrity constraint: {}", id, ex.getMessage());
            throw new BusinessException("Không thể xóa tài khoản này do có dữ liệu liên kết nghiệp vụ (chấm công, lịch trực, hoặc chứng từ kế toán). Vui lòng sử dụng tính năng Khóa tài khoản.");
        }
    }

    private UserResponseDto mapToDto(User user) {
        int totalBookings = userRepository.countBookingsByUserId(user.getId());
        double totalSpent = userRepository.sumTotalSpentByUserId(user.getId());
        LocalDateTime lastBookingDate = userRepository.findLastBookingDateByUserId(user.getId());

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .totalBookings(totalBookings)
                .totalSpent(totalSpent)
                .lastBookingDate(lastBookingDate)
                .build();
    }
}
