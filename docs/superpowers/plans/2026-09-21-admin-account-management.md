# Kế Hoạch Triển Khai: Phân Hệ Quản Lý Tài Khoản Cho Admin (Unified Account Management Hub)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Xây dựng toàn diện phân hệ Quản Lý Tài Khoản (`/admin/users`) cho Admin với Spring Boot REST API và Angular UI Luxury Theme, hỗ trợ phân chia 2 tab [Khách Hàng VIP] & [Nhân Sự Nội Bộ], đầy đủ tính năng tạo tài khoản, đổi vai trò, khóa/mở khóa, reset mật khẩu, tìm kiếm & phân trang kết nối trực tiếp MySQL.

**Architecture:** Spring Boot cung cấp `AdminUserController` (`/api/v1/admin/users`) và `AdminUserService` tương tác với `UserRepository` và `User` entity, tích hợp Spring Security BCrypt và an toàn chống tự khóa tài khoản Admin. Frontend Angular cung cấp `UserManagementComponent` độc lập với kiến trúc 2 tab, 4 thẻ Metric Cards, bộ lọc tìm kiếm tức thời, và các modal tương tác.

**Tech Stack:** Java 17, Spring Boot 3, Spring Security, BCrypt, JPA / Hibernate, MySQL, Angular 17+, Tailwind CSS, Material Symbols.

## Global Constraints

- Backend context-path: `/api/v1` (URL: `http://localhost:8080/api/v1/admin/users`).
- Frontend theme: Luxury Resort Dashboard (bảng màu Slate, Sky #0284c7, Emerald, Indigo, Amber, Rose).
- Zero Mock Data: 100% dữ liệu được truy vấn và thao tác trực tiếp từ cơ sở dữ liệu MySQL thông qua API.
- Password Security: Mật khẩu lưu vào DB bắt buộc băm bằng BCrypt qua `PasswordEncoder`. DTO trả về không bao giờ để lộ trường `password`.
- Self-lockout Protection: Không cho phép tài khoản Admin tự khóa hoặc tự tước quyền của chính mình.

---

### Task 1: Backend DTOs & Repository Enhancement

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UserResponseDto.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UserSummaryStatsDto.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/CreateUserAdminRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UpdateUserAdminRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/ResetPasswordAdminRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UserFilterRequest.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/UserRepository.java`

**Interfaces:**
- Produces: Các DTOs trong `com.phungvanlong.booking_hotel.dto.user.*` và các phương thức truy vấn mới trong `UserRepository`.

- [ ] **Step 1: Tạo các DTOs cho nghiệp vụ quản lý tài khoản**

Tạo thư mục và file DTO:
`backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UserResponseDto.java`:
```java
package com.phungvanlong.booking_hotel.dto.user;

import com.phungvanlong.booking_hotel.entity.AuthProvider;
import com.phungvanlong.booking_hotel.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private Boolean isActive;
    private AuthProvider provider;
    private LocalDateTime createdAt;
    private Integer totalBookings;
    private Double totalSpent;
    private LocalDateTime lastBookingDate;
}
```

`backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UserSummaryStatsDto.java`:
```java
package com.phungvanlong.booking_hotel.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryStatsDto {
    private long totalUsers;
    private long totalCustomers;
    private long totalStaff;
    private long totalLocked;
}
```

`backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/CreateUserAdminRequest.java`:
```java
package com.phungvanlong.booking_hotel.dto.user;

import com.phungvanlong.booking_hotel.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserAdminRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String phone;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}
```

`backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UpdateUserAdminRequest.java`:
```java
package com.phungvanlong.booking_hotel.dto.user;

import com.phungvanlong.booking_hotel.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserAdminRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String phone;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}
```

`backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/ResetPasswordAdminRequest.java`:
```java
package com.phungvanlong.booking_hotel.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordAdminRequest {
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String newPassword;
}
```

`backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/user/UserFilterRequest.java`:
```java
package com.phungvanlong.booking_hotel.dto.user;

import com.phungvanlong.booking_hotel.entity.Role;
import lombok.Data;

@Data
public class UserFilterRequest {
    private String tab; // "CUSTOMER", "STAFF", or "ALL"
    private String search;
    private Role role;
    private Boolean isActive;
    private int page = 0;
    private int size = 10;
}
```

- [ ] **Step 2: Cập nhật `UserRepository` với các query count và tìm kiếm**

Cập nhật `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/UserRepository.java`:
```java
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

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    long countByRole(Role role);

    long countByRoleNot(Role role);

    long countByIsActiveFalse();

    @Query("SELECT u FROM User u WHERE " +
            "(:tab = 'CUSTOMER' AND u.role = 'ROLE_CUSTOMER' OR " +
            " :tab = 'STAFF' AND u.role != 'ROLE_CUSTOMER' OR " +
            " :tab IS NULL OR :tab = 'ALL') AND " +
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
}
```

- [ ] **Step 3: Biên dịch Backend để kiểm tra cú pháp**

Run:
```powershell
cd d:\booking_hotel\backend\booking_hotel
.\mvnw.cmd compile -q
```
Expected: Biên dịch thành công với code 0.

---

### Task 2: Backend Service Layer (`AdminUserService`)

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/AdminUserService.java`

**Interfaces:**
- Consumes: `UserRepository`, `PasswordEncoder`, DTOs từ Task 1.
- Produces: Các phương thức xử lý nghiệp vụ cho `AdminUserController`.

- [ ] **Step 1: Tạo `AdminUserService.java`**

Tạo `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/AdminUserService.java`:
```java
package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.user.*;
import com.phungvanlong.booking_hotel.entity.Booking;
import com.phungvanlong.booking_hotel.entity.BookingStatus;
import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.exception.BadRequestException;
import com.phungvanlong.booking_hotel.exception.DuplicateResourceException;
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
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' đã được sử dụng trong hệ thống.");
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty() && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại '" + request.getPhone() + "' đã được sử dụng.");
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
            throw new BadRequestException("Không thể tự thay đổi vai trò của tài khoản Admin đang đăng nhập.");
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (!request.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Số điện thoại '" + request.getPhone() + "' đã được sử dụng bởi tài khoản khác.");
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
            throw new BadRequestException("Không thể tự khóa tài khoản của chính mình.");
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
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
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
```

- [ ] **Step 2: Biên dịch và xác nhận không có lỗi trong `AdminUserService`**

Run:
```powershell
cd d:\booking_hotel\backend\booking_hotel
.\mvnw.cmd compile -q
```
Expected: Biên dịch thành công với code 0.

---

### Task 3: Backend Controller Layer (`AdminUserController`)

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminUserController.java`

**Interfaces:**
- Consumes: `AdminUserService`, Spring Security `Authentication`.
- Produces: REST endpoints dưới prefix `/admin/users`.

- [ ] **Step 1: Tạo `AdminUserController.java`**

Tạo `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminUserController.java`:
```java
package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.user.*;
import com.phungvanlong.booking_hotel.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> getUsers(
            @ModelAttribute UserFilterRequest filter) {
        PageResponse<UserResponseDto> users = adminUserService.getUsers(filter);
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách người dùng thành công"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserSummaryStatsDto>> getUserStats() {
        UserSummaryStatsDto stats = adminUserService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê người dùng thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = adminUserService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy chi tiết người dùng thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
            @Valid @RequestBody CreateUserAdminRequest request) {
        UserResponseDto created = adminUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Tạo tài khoản người dùng thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserAdminRequest request,
            Authentication authentication) {
        String currentAdminEmail = authentication != null ? authentication.getName() : "";
        UserResponseDto updated = adminUserService.updateUser(id, request, currentAdminEmail);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật thông tin người dùng thành công"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> toggleUserStatus(
            @PathVariable Long id,
            Authentication authentication) {
        String currentAdminEmail = authentication != null ? authentication.getName() : "";
        UserResponseDto updated = adminUserService.toggleUserStatus(id, currentAdminEmail);
        String action = Boolean.TRUE.equals(updated.getIsActive()) ? "Kích hoạt" : "Khóa";
        return ResponseEntity.ok(ApiResponse.success(updated, action + " tài khoản thành công"));
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordAdminRequest request) {
        adminUserService.resetPassword(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Đặt lại mật khẩu thành công"));
    }
}
```

- [ ] **Step 2: Biên dịch Backend toàn bộ và xác thực**

Run:
```powershell
cd d:\booking_hotel\backend\booking_hotel
.\mvnw.cmd compile -q
```
Expected: Biên dịch thành công với code 0.

---

### Task 4: Frontend Data Models & `AdminUserService`

**Files:**
- Create: `frontend/src/app/core/models/admin-user.model.ts`
- Create: `frontend/src/app/core/services/admin-user.service.ts`

**Interfaces:**
- Produces: `AdminUser`, `UserSummaryStats`, `CreateUserRequest`, `UpdateUserRequest`, `ResetPasswordRequest`, `UserFilter`, và `AdminUserService`.

- [ ] **Step 1: Tạo TypeScript Models**

Tạo `frontend/src/app/core/models/admin-user.model.ts`:
```typescript
export type UserRole = 
  | 'ROLE_CUSTOMER' 
  | 'ROLE_STAFF' 
  | 'ROLE_RECEPTIONIST' 
  | 'ROLE_BUTLER' 
  | 'ROLE_HOUSEKEEPING' 
  | 'ROLE_ACCOUNTANT' 
  | 'ROLE_ADMIN';

export type AuthProvider = 'LOCAL' | 'GOOGLE' | 'FACEBOOK';

export interface AdminUser {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  role: UserRole;
  isActive: boolean;
  provider: AuthProvider;
  createdAt: string;
  totalBookings?: number;
  totalSpent?: number;
  lastBookingDate?: string;
}

export interface UserSummaryStats {
  totalUsers: number;
  totalCustomers: number;
  totalStaff: number;
  totalLocked: number;
}

export interface CreateUserRequest {
  email: string;
  password?: string;
  fullName: string;
  phone?: string;
  role: UserRole;
}

export interface UpdateUserRequest {
  fullName: string;
  phone?: string;
  role: UserRole;
}

export interface ResetPasswordRequest {
  newPassword: string;
}

export interface UserFilter {
  tab: 'CUSTOMER' | 'STAFF' | 'ALL';
  search?: string;
  role?: UserRole | '';
  isActive?: boolean | '';
  page: number;
  size: number;
}

export interface PageResponse<T> {
  content: T[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}
```

- [ ] **Step 2: Tạo `AdminUserService`**

Tạo `frontend/src/app/core/services/admin-user.service.ts`:
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  AdminUser,
  ApiResponse,
  CreateUserRequest,
  PageResponse,
  ResetPasswordRequest,
  UpdateUserRequest,
  UserFilter,
  UserSummaryStats,
} from '../models/admin-user.model';

@Injectable({
  providedIn: 'root',
})
export class AdminUserService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/admin/users';

  constructor(private http: HttpClient) {}

  getUsers(filter: UserFilter): Observable<PageResponse<AdminUser>> {
    let params = new HttpParams()
      .set('tab', filter.tab)
      .set('page', filter.page.toString())
      .set('size', filter.size.toString());

    if (filter.search && filter.search.trim()) {
      params = params.set('search', filter.search.trim());
    }
    if (filter.role) {
      params = params.set('role', filter.role);
    }
    if (filter.isActive !== '' && filter.isActive !== undefined && filter.isActive !== null) {
      params = params.set('isActive', filter.isActive.toString());
    }

    return this.http
      .get<ApiResponse<PageResponse<AdminUser>>>(this.baseUrl, { params })
      .pipe(map((res) => res.data));
  }

  getStats(): Observable<UserSummaryStats> {
    return this.http
      .get<ApiResponse<UserSummaryStats>>(`${this.baseUrl}/stats`)
      .pipe(map((res) => res.data));
  }

  getUserById(id: number): Observable<AdminUser> {
    return this.http
      .get<ApiResponse<AdminUser>>(`${this.baseUrl}/${id}`)
      .pipe(map((res) => res.data));
  }

  createUser(request: CreateUserRequest): Observable<AdminUser> {
    return this.http
      .post<ApiResponse<AdminUser>>(this.baseUrl, request)
      .pipe(map((res) => res.data));
  }

  updateUser(id: number, request: UpdateUserRequest): Observable<AdminUser> {
    return this.http
      .put<ApiResponse<AdminUser>>(`${this.baseUrl}/${id}`, request)
      .pipe(map((res) => res.data));
  }

  toggleUserStatus(id: number): Observable<AdminUser> {
    return this.http
      .patch<ApiResponse<AdminUser>>(`${this.baseUrl}/${id}/status`, {})
      .pipe(map((res) => res.data));
  }

  resetPassword(id: number, request: ResetPasswordRequest): Observable<void> {
    return this.http
      .patch<ApiResponse<void>>(`${this.baseUrl}/${id}/reset-password`, request)
      .pipe(map(() => void 0));
  }
}
```

- [ ] **Step 3: Kiểm tra biên dịch TypeScript**

Run:
```powershell
cd d:\booking_hotel\frontend
npx tsc --noEmit
```
Expected: Không có lỗi type.

---

### Task 5: Frontend Component Implementation (`UserManagementComponent`)

**Files:**
- Create: `frontend/src/app/admin/user-management/user-management.component.ts`
- Create: `frontend/src/app/admin/user-management/user-management.component.html`
- Create: `frontend/src/app/admin/user-management/user-management.component.css`

**Interfaces:**
- Consumes: `AdminUserService`, `AdminUser`, `UserSummaryStats`.
- Produces: Giao diện quản lý tài khoản 2 tab, 4 thẻ metric, bảng danh sách, phân trang, các dialog tạo/sửa/đổi mật khẩu/chi tiết/xác nhận khóa.

- [ ] **Step 1: Tạo `user-management.component.ts`**

Xây dựng logic Component với:
- Reactive state: `users`, `stats`, `isLoading`, `toastMessage`, `currentTab` ('CUSTOMER' | 'STAFF').
- Search filter có debounce: `searchTerm`, `selectedRole`, `selectedStatus`.
- Modal states:
  - `showCreateModal`: Thêm tài khoản
  - `showEditModal`: Sửa thông tin & vai trò
  - `showResetPasswordModal`: Đặt lại mật khẩu
  - `showDetailModal`: Xem thông tin chi tiết & lịch sử đặt phòng
  - `showConfirmStatusModal`: Xác nhận khóa/mở khóa
- Pagination: `pageNo`, `pageSize`, `totalPages`, `totalElements`.
- Utility methods: `getRoleBadgeClass(role)`, `getRoleLabel(role)`, `getAvatarBg(name)`, `getInitials(name)`.

- [ ] **Step 2: Tạo `user-management.component.html`**

Thiết kế giao diện chuẩn phong cách Luxury Hotel Resort:
- **Breadcrumb & Header**: Tiêu đề "QUẢN LÝ TÀI KHOẢN NGƯỜI DÙNG", nút "Thêm Mới Tài Khoản", nút làm mới dữ liệu.
- **4 Thẻ Chỉ Số (KPI Metric Cards)**:
  - Thẻ 1: Tổng người dùng (icon `groups`, màu gradient xanh cyan/sky)
  - Thẻ 2: Khách hàng VIP (icon `person`, màu emerald)
  - Thẻ 3: Đội ngũ nhân viên (icon `badge`, màu tím indigo)
  - Thẻ 4: Tài khoản bị khóa (icon `lock`, màu rose/đỏ)
- **Tab Navigation**:
  - `[Tab 1: Khách Hàng & Hội Viên VIP]`
  - `[Tab 2: Đội Ngũ Nhân Viên & Ban Quản Trị]`
- **Filter Toolbar**:
  - Input tìm kiếm tức thời theo Tên, Email, SĐT.
  - Dropdown chọn vai trò (chỉ hiển thị các vai trò nhân viên khi ở tab Nhân sự).
  - Dropdown chọn trạng thái (Tất cả / Đang hoạt động / Đã khóa).
- **Bảng Dữ Liệu Tương Tác**:
  - Avatar người dùng tự sinh với chữ cái đầu và màu nền ngẫu nhiên theo tên.
  - Cột Họ tên & Email, SĐT, Vai trò, Ngày tham gia, Trạng thái (Active/Locked toggle).
  - Cột riêng cho Khách hàng: Tổng đơn đặt (`totalBookings`) và Chi tiêu (`totalSpent` format VNĐ).
  - Cột Thao tác: Menu nút Chi tiết, Sửa, Đổi mật khẩu, Khóa/Mở khóa.
- **Phân trang hoàn chỉnh**:
  - Nút Trang trước / Trang sau, chọn số lượng hiển thị (10, 25, 50), hiển thị "Hiển thị X - Y trên tổng số Z tài khoản".
- **5 Modals Tương Tác Hoàn Chỉnh**:
  - Modal Tạo Mới với validation đầy đủ.
  - Modal Sửa Thông Tin & Vai Trò.
  - Modal Đặt Lại Mật Khẩu (yêu cầu xác nhận mật khẩu mới).
  - Modal Chi Tiết Hồ Sơ Khách Hàng / Nhân Viên.
  - Modal Xác Nhận Khóa / Mở Khóa với cảnh báo trực quan.
- **Toast Notification**: Hiển thị góc trên phải thông báo kết quả các hành động.

- [ ] **Step 3: Tạo `user-management.component.css`**

Thêm các animation và scrollbar tùy biến cho bảng và modal.

- [ ] **Step 4: Kiểm tra build Frontend**

Run:
```powershell
cd d:\booking_hotel\frontend
npm run build
```
Expected: Build thành công không có lỗi cú pháp.

---

### Task 6: Routing & Sidebar Navigation Integration

**Files:**
- Modify: `frontend/src/app/app.routes.ts`
- Modify: `frontend/src/app/admin/layout/admin-layout.component.html`

**Interfaces:**
- Consumes: `UserManagementComponent`.
- Produces: Đường dẫn `/admin/users` hoạt động và mục menu "Quản Lý Tài Khoản" trên thanh điều hướng bên trái của Admin.

- [ ] **Step 1: Đăng ký Route `/admin/users` trong `app.routes.ts`**

Chèn route con trong `admin` layout:
```typescript
{
  path: 'users',
  loadComponent: () =>
    import('./admin/user-management/user-management.component').then(
      (m) => m.UserManagementComponent
    ),
},
```

- [ ] **Step 2: Cập nhật menu Sidebar trong `admin-layout.component.html`**

Thay thế mục `Khách Hàng & CRM` (hiện đang trỏ tới `#vip-crm`) thành mục chính thức:
```html
<!-- 4. Quản Lý Tài Khoản -->
<a routerLink="/admin/users" routerLinkActive="bg-[#0284c7] text-white font-bold shadow-sm"
   class="flex items-center px-3 py-2.5 rounded-xl text-xs font-semibold text-slate-600 hover:text-slate-900 hover:bg-slate-100/80 transition-all group">
  <span class="material-symbols-outlined text-[18px] mr-2.5 text-slate-400 group-hover:text-slate-700 transition-colors">manage_accounts</span>
  <span>Quản Lý Tài Khoản</span>
</a>
```

- [ ] **Step 3: Kiểm tra build Frontend sau khi tích hợp**

Run:
```powershell
cd d:\booking_hotel\frontend
npm run build
```
Expected: Build thành công với code 0.

---

### Task 7: End-to-End Verification & Real Database Integration Test

**Files:**
- Verification only

- [ ] **Step 1: Khởi động Backend Spring Boot và kiểm tra database kết nối**

Run:
```powershell
cd d:\booking_hotel\backend\booking_hotel
.\mvnw.cmd spring-boot:run
```
Expected: Ứng dụng khởi động thành công trên cổng 8080.

- [ ] **Step 2: Kiểm tra các REST API `/api/v1/admin/users` qua HTTP requests**

Kiểm tra:
- `GET http://localhost:8080/api/v1/admin/users/stats` -> Trả về JSON 200 với các thống kê số lượng.
- `GET http://localhost:8080/api/v1/admin/users?tab=CUSTOMER` -> Trả về danh sách khách hàng.
- `GET http://localhost:8080/api/v1/admin/users?tab=STAFF` -> Trả về danh sách nhân viên.
- `POST http://localhost:8080/api/v1/admin/users` -> Tạo thử 1 nhân viên test với role `ROLE_RECEPTIONIST`.
- `PATCH http://localhost:8080/api/v1/admin/users/{id}/status` -> Khóa tài khoản test vừa tạo.
- `PATCH http://localhost:8080/api/v1/admin/users/{id}/reset-password` -> Đặt mật khẩu mới.

- [ ] **Step 3: Kiểm tra giao diện người dùng trên trình duyệt (Browser Test)**

Điều hướng tới `http://localhost:4200/admin/users`:
- Kiểm tra hiển thị 4 thẻ chỉ số KPI.
- Chuyển đổi qua lại giữa Tab Khách hàng & Tab Nhân sự.
- Kiểm tra tìm kiếm theo tên, lọc vai trò.
- Bấm mở modal tạo mới tài khoản và submit dữ liệu thực tế.
- Bấm Khóa/Mở khóa và kiểm tra badge trạng thái cập nhật ngay lập tức.
- Kiểm tra click từ menu Sidebar sang các trang khác và quay lại `/admin/users`.
