package com.phungvanlong.booking_hotel.service;

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
import com.phungvanlong.booking_hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với mã ID: " + id));
        return mapToDto(user);
    }

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

    @Transactional
    public void resetPassword(Long id, ResetPasswordAdminRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Admin reset password for user ID={}", user.getId());
    }

    private UserResponseDto mapToDto(User user) {
        List<Booking> bookings = user.getBookings();
        int totalBookings = bookings != null ? bookings.size() : 0;
        
        double totalSpent = 0.0;
        LocalDateTime lastBookingDate = null;

        if (bookings != null && !bookings.isEmpty()) {
            totalSpent = bookings.stream()
                    .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                    .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0.0)
                    .sum();

            lastBookingDate = bookings.stream()
                    .map(Booking::getCreatedAt)
                    .filter(d -> d != null)
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }

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
