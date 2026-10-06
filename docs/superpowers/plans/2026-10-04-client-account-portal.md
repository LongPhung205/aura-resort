# Client Account Portal Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a complete client account portal at `/account` encompassing "Chuyến đi của tôi" (My Bookings), "Đánh giá & Nhận xét" (My Reviews), and "Thông tin Tài khoản" (My Profile) with full backend API support and responsive, luxury resort UI styling.

**Architecture:** A standalone Angular container component `AccountComponent` at `/account` (routed with `authGuard`) featuring a unified sidebar and tab views (`tab=bookings`, `tab=reviews`, `tab=profile`). The frontend talks to existing and new Spring Boot REST controllers (`UserController`, `ReviewController`, `BookingController`) secured with JWT `Authentication`.

**Tech Stack:**
- Frontend: Angular 19+ (Standalone components, Signals / Reactive programming, Tailwind CSS, Lucide / Material Symbols)
- Backend: Spring Boot 3+ (Java 21, Spring Security, JPA/Hibernate, MySQL)

## Global Constraints
- Preserve existing comments and docstrings.
- Adhere to the established luxury branding (`#0369A1` / `#0284C7` Ocean Blue, `#EA580C` Amber accents, Slate 50-900 neutrals).
- Endpoints must validate authentication and prevent unauthorized cross-tenant data access.
- Code must pass `npx tsc --noEmit` on frontend and compile cleanly on backend.

---

### Task 1: Backend - User Profile Controller & DTOs

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/UserController.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/UpdateProfileRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/ChangePasswordRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/UserProfileResponse.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/SecurityConfig.java`

**Interfaces:**
- Produces:
  - `GET /api/v1/users/me` -> `ApiResponse<UserProfileResponse>`
  - `PUT /api/v1/users/me` -> `ApiResponse<UserProfileResponse>`
  - `PUT /api/v1/users/me/password` -> `ApiResponse<Void>`

- [ ] **Step 1: Create `UserProfileResponse.java`**

```java
package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.AuthProvider;
import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private AuthProvider provider;
    private LocalDateTime createdAt;
    private Integer totalBookings;
    private Double totalSpent;

    public static UserProfileResponse fromEntity(User user, int totalBookings, double totalSpent) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .totalBookings(totalBookings)
                .totalSpent(totalSpent)
                .build();
    }
}
```

- [ ] **Step 2: Create `UpdateProfileRequest.java` and `ChangePasswordRequest.java`**

```java
package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private String phone;
}
```

```java
package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String currentPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String newPassword;
}
```

- [ ] **Step 3: Create `UserController.java`**

```java
package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ChangePasswordRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateProfileRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.UserProfileResponse;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.exception.AppException;
import com.phungvanlong.booking_hotel.exception.ErrorCode;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        int totalBookings = userRepository.countBookingsByUserId(user.getId());
        double totalSpent = userRepository.sumTotalSpentByUserId(user.getId());

        UserProfileResponse response = UserProfileResponse.fromEntity(user, totalBookings, totalSpent);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin tài khoản thành công"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(request.getFullName().trim());
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        User saved = userRepository.save(user);

        int totalBookings = userRepository.countBookingsByUserId(saved.getId());
        double totalSpent = userRepository.sumTotalSpentByUserId(saved.getId());

        UserProfileResponse response = UserProfileResponse.fromEntity(saved, totalBookings, totalSpent);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thông tin thành công"));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công"));
    }
}
```

- [ ] **Step 4: Update `SecurityConfig.java` to allow authenticated users on `/users/**`**
Verify that `/users/**` is accessible by authenticated users.

---

### Task 2: Backend - My Reviews API & Response Enrichment

**Files:**
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/ReviewRepository.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/ReviewResponse.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/ReviewService.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/ReviewServiceImpl.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/ReviewController.java`

**Interfaces:**
- Produces: `GET /api/v1/reviews/me` -> `ApiResponse<List<ReviewResponse>>`

- [ ] **Step 1: Update `ReviewRepository.java`**
Add query method:
```java
@Query("SELECT r FROM Review r WHERE r.booking.user.email = :email ORDER BY r.createdAt DESC")
List<Review> findByUserEmail(@Param("email") String email);
```

- [ ] **Step 2: Enrich `ReviewResponse.java`**
Add fields:
```java
private Long bookingId;
private String villaName;
private String sentiment;
private String managementReply;
private LocalDateTime repliedAt;
```
Populate these fields in `fromEntity(Review entity)`.

- [ ] **Step 3: Update `ReviewService.java` & `ReviewServiceImpl.java`**
Add `List<ReviewResponse> getMyReviews(String userEmail)` in interface and implement it in service:
```java
@Override
public List<ReviewResponse> getMyReviews(String userEmail) {
    return reviewRepository.findByUserEmail(userEmail)
            .stream()
            .map(ReviewResponse::fromEntity)
            .collect(Collectors.toList());
}
```

- [ ] **Step 4: Add endpoint in `ReviewController.java`**
```java
@GetMapping("/me")
public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(Authentication authentication) {
    String userEmail = authentication.getName();
    List<ReviewResponse> reviews = reviewService.getMyReviews(userEmail);
    return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá của tôi thành công"));
}
```

---

### Task 3: Frontend - Client Services & Data Models

**Files:**
- Create: `frontend/src/app/core/services/user-profile.service.ts`
- Modify: `frontend/src/app/core/services/review.service.ts`
- Modify: `frontend/src/app/core/services/booking.service.ts`

- [ ] **Step 1: Create `user-profile.service.ts`**
Provide methods:
- `getProfile(): Observable<UserProfile>`
- `updateProfile(data: { fullName: string; phone: string }): Observable<UserProfile>`
- `changePassword(data: { currentPassword: string; newPassword: string }): Observable<void>`

- [ ] **Step 2: Update `review.service.ts`**
Add `getMyReviews(): Observable<ReviewResponse[]>` and `createReview(request: ReviewRequest): Observable<ReviewResponse>`.

- [ ] **Step 3: Verify `booking.service.ts`**
Ensure `getMyBookings()` and `cancelBooking(id: number)` return standard observables.

---

### Task 4: Frontend - Account Portal Shell Component & Routing

**Files:**
- Create: `frontend/src/app/features/account/account.component.ts`
- Create: `frontend/src/app/features/account/account.component.html`
- Create: `frontend/src/app/features/account/account.component.scss`
- Modify: `frontend/src/app/app.routes.ts`
- Modify: `frontend/src/app/layout/main-layout/header/header.component.html`

- [ ] **Step 1: Register route in `app.routes.ts`**
```typescript
{
  path: 'account',
  loadComponent: () => import('./features/account/account.component').then(m => m.AccountComponent),
  canActivate: [authGuard]
},
```

- [ ] **Step 2: Update Header user menu in `header.component.html`**
Replace generic links with direct routes to `/account?tab=bookings`, `/account?tab=reviews`, `/account?tab=profile`.

---

### Task 5: Frontend - My Bookings Tab ("Chuyến đi của tôi")

**Files:**
- Modify: `frontend/src/app/features/account/account.component.html`
- Modify: `frontend/src/app/features/account/account.component.ts`

- [ ] **Step 1: Implement Metrics Counter & Tab Filter**
Counters for `Total`, `Upcoming`, `Completed`, `Cancelled`.
Tabs for status switching: `ALL`, `PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`.

- [ ] **Step 2: Implement Booking Card List**
Display booking code, check-in / check-out dates, nights count, total amount, status badge, action buttons.

- [ ] **Step 3: Implement Booking Detail / Invoice Modal**
Popup showing breakdown of stay, services, villa details, and print action.

- [ ] **Step 4: Implement Cancel Booking Modal**
Confirmation modal with warning dialog calling `bookingService.cancelBooking(id)`.

---

### Task 6: Frontend - My Reviews Tab ("Đánh giá & Nhận xét")

**Files:**
- Modify: `frontend/src/app/features/account/account.component.html`
- Modify: `frontend/src/app/features/account/account.component.ts`

- [ ] **Step 1: Reviewed List**
Display star rating (1-5★), review date, comment, and management reply quote box if replied.

- [ ] **Step 2: Pending Reviews List**
List completed bookings with a prominent "Đánh giá ngay" CTA.

- [ ] **Step 3: Write Review Modal**
Star selector (1 to 5), textarea for comment, and submission handling via `ReviewService`.

---

### Task 7: Frontend - My Profile Tab ("Thông tin Tài khoản")

**Files:**
- Modify: `frontend/src/app/features/account/account.component.html`
- Modify: `frontend/src/app/features/account/account.component.ts`

- [ ] **Step 1: Profile Details Form**
Initials avatar, Email (disabled), Full name, Phone number, with "Lưu thay đổi" button and toast notification.

- [ ] **Step 2: Change Password Form**
Inputs for current password, new password, confirm new password, with client-side validation and toast feedback.

---

### Task 8: Verification & Quality Assurance

- [ ] **Step 1: Compile Backend Code**
Verify all Java classes compile cleanly without errors.

- [ ] **Step 2: Run Frontend Typecheck**
Run `npx tsc --noEmit` in `frontend/` to confirm 0 TypeScript compiler errors.

- [ ] **Step 3: Manual / Automated Verification**
Verify page navigation, bookings list, review submission, and profile update.
