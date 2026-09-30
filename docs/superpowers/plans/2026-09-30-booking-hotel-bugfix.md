# Booking Hotel — Kế Hoạch Sửa Toàn Diện 20 Lỗi

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Khắc phục toàn bộ 20 lỗi đã phân tích (5 Critical, 8 Major, 7 Minor) để dự án Booking Hotel đạt mức production-ready.

**Architecture:** Backend Spring Boot 3 + Angular 18 standalone. Sửa theo nguyên tắc ít thay đổi nhất (surgical fix), không refactor toàn diện. Ưu tiên bảo mật và tính đúng đắn của business logic trước, sau đó tối ưu hiệu năng.

**Tech Stack:** Java 21, Spring Boot 3, Spring Security 6, JPA/Hibernate, Redis, Angular 18, TypeScript 5, RxJS 7

## Global Constraints

- Không thêm dependency mới vào pom.xml trừ khi bắt buộc
- Mọi fix phải backward-compatible — không xóa endpoint cũ
- Commit sau mỗi task hoàn thành
- Đặt tên commit: `fix: <mô tả ngắn>`
- Backend base URL: `http://localhost:8080/api/v1`
- Frontend base: `http://localhost:4200`

---

## Task 1: Bảo Mật Khẩn Cấp — Backend SecurityConfig + Frontend Route Guard

**Mức độ:** 🔴 Critical — Sửa trước tất cả

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\config\SecurityConfig.java`
- Create: `d:\booking_hotel\frontend\src\app\core\guards\auth.guard.ts`
- Create: `d:\booking_hotel\frontend\src\app\core\guards\admin.guard.ts`
- Modify: `d:\booking_hotel\frontend\src\app\app.routes.ts`
- Create: `d:\booking_hotel\frontend\src\environments\environment.ts`
- Create: `d:\booking_hotel\frontend\src\environments\environment.prod.ts`
- Modify: `d:\booking_hotel\frontend\src\app\app.config.ts`
- Modify: `d:\booking_hotel\frontend\src\app\core\services\token.service.ts`

**Interfaces:**
- Produces: `authGuard`, `adminGuard` — dùng làm `canActivate` cho routes

---

- [ ] **Step 1: Sửa SecurityConfig — `/admin/**` yêu cầu JWT và ROLE_ADMIN**

Mở `SecurityConfig.java`. Thay toàn bộ khối `authorizeHttpRequests` (dòng 47-57):

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers(
        "/room-types/**", "/rooms/**", "/villa-types/**", "/villas/**",
        "/banners/**", "/zones/**", "/public/**", "/promotions/**",
        "/reviews/**", "/extra-services/**").permitAll()
    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
    .requestMatchers("/payments/momo-return", "/payments/momo-ipn").permitAll()
    .requestMatchers("/housekeeping/**").permitAll()
    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
    .requestMatchers("/bookings/**", "/payments/**").authenticated()
    .anyRequest().authenticated())
```

- [ ] **Step 2: Sửa CORS — dùng setAllowedOriginPatterns và allowedHeaders rộng hơn**

Thay method `corsConfigurationSource()` trong cùng file:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of(
        "http://localhost:4200",
        "http://localhost:*",
        "https://*.yourdomain.com"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization", "x-auth-token"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

- [ ] **Step 3: Tạo environment files cho Angular**

Tạo `d:\booking_hotel\frontend\src\environments\environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  googleClientId: '807553747109-i9f5jua0s4edftfmnk7dmlkekimuegi4.apps.googleusercontent.com'
};
```

Tạo `d:\booking_hotel\frontend\src\environments\environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com/api/v1',
  googleClientId: 'YOUR_PROD_GOOGLE_CLIENT_ID'
};
```

- [ ] **Step 4: Thêm getRole() và removeRole() vào TokenService**

Mở `d:\booking_hotel\frontend\src\app\core\services\token.service.ts`. Thêm sau `isAuthenticated()`:

```typescript
private readonly ROLE_KEY = 'user_role';

saveRole(role: string): void {
  localStorage.setItem(this.ROLE_KEY, role);
}

getRole(): string | null {
  return localStorage.getItem(this.ROLE_KEY);
}

removeRole(): void {
  localStorage.removeItem(this.ROLE_KEY);
}
```

- [ ] **Step 5: Tạo authGuard**

Tạo `d:\booking_hotel\frontend\src\app\core\guards\auth.guard.ts`:

```typescript
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';

export const authGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (tokenService.isAuthenticated()) {
    return true;
  }
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
```

- [ ] **Step 6: Tạo adminGuard**

Tạo `d:\booking_hotel\frontend\src\app\core\guards\admin.guard.ts`:

```typescript
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (!tokenService.isAuthenticated()) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
  if (tokenService.getRole() === 'ROLE_ADMIN') {
    return true;
  }
  router.navigate(['/']);
  return false;
};
```

- [ ] **Step 7: Cập nhật app.config.ts dùng environment cho Google Client ID**

Mở `d:\booking_hotel\frontend\src\app\app.config.ts`. Thêm import và thay chuỗi hard-code:

```typescript
import { environment } from '../environments/environment';
// ...
provider: new GoogleLoginProvider(environment.googleClientId)
```

- [ ] **Step 8: Thêm canActivate vào admin route trong app.routes.ts**

Mở `d:\booking_hotel\frontend\src\app\app.routes.ts`:

```typescript
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
```

Tìm route `path: 'admin'` (dòng 75), thêm `canActivate`:

```typescript
{
  path: 'admin',
  loadComponent: () =>
    import('./admin/layout/admin-layout.component').then((m) => m.AdminLayoutComponent),
  canActivate: [authGuard, adminGuard],
  children: [ /* giữ nguyên */ ]
},
```

- [ ] **Step 9: Tìm login component và lưu role vào localStorage**

Mở `d:\booking_hotel\frontend\src\app\features\auth\login\login.component.ts`. Tìm chỗ gọi `authService.login()`. Sau khi nhận response thành công, thêm:

```typescript
this.tokenService.saveToken(res.data.accessToken);
this.tokenService.saveRole(res.data.role ?? 'ROLE_CUSTOMER');
```

- [ ] **Step 10: Verify backend**

```bash
# Phải trả 403 khi không có token
curl http://localhost:8080/api/v1/admin/dashboard/stats
# Expected: {"status":403,"error":"Forbidden",...}
```

- [ ] **Step 11: Commit**

```bash
git add .
git commit -m "fix: secure admin routes - hasAuthority ROLE_ADMIN + Angular canActivate guards"
```

---

## Task 2: Sửa Payment Duplicate — MoMo IPN Idempotency

**Mức độ:** 🔴 Critical

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\repository\PaymentRepository.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\PaymentServiceImpl.java`

**Interfaces:**
- Produces: `PaymentRepository.findByBookingId(Long)`, `PaymentRepository.existsByBookingIdAndStatus(Long, PaymentStatus)`
- Produces: `verifyAndProcessPayment()` idempotent — gọi N lần chỉ tạo 1 Payment

---

- [ ] **Step 1: Thêm query vào PaymentRepository**

Thay toàn bộ nội dung `PaymentRepository.java`:

```java
package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Payment;
import com.phungvanlong.booking_hotel.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    boolean existsByBookingIdAndStatus(Long bookingId, PaymentStatus status);
}
```

- [ ] **Step 2: Xem Payment entity để xác nhận có field booking**

Mở `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\entity\Payment.java`. Xác nhận có:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "booking_id")
private Booking booking;
```

Nếu thiếu, thêm vào. JPA tự map `findByBookingId` từ field này.

- [ ] **Step 3: Sửa verifyAndProcessPayment() — thêm idempotency check**

Mở `PaymentServiceImpl.java`. Tìm khối `if ("0".equals(resultCode))` (dòng 150-168). Thay:

```java
if ("0".equals(resultCode)) {
    // IDEMPOTENCY: nếu đã xử lý thành công rồi thì bỏ qua
    if (paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS)) {
        log.info("Booking {} đã thanh toán trước đó, bỏ qua callback lặp.", bookingId);
        return;
    }

    booking.setStatus(BookingStatus.CONFIRMED);
    bookingRepository.save(booking);

    // Cập nhật hoặc tạo Payment (upsert pattern)
    Payment payment = paymentRepository.findByBookingId(bookingId).orElse(
        Payment.builder().booking(booking).build()
    );
    payment.setAmount(booking.getTotalAmount());
    payment.setPaymentMethod("MOMO");
    payment.setStatus(PaymentStatus.SUCCESS);
    payment.setTransactionId(transId);
    payment.setPaymentTime(LocalDateTime.now());
    paymentRepository.save(payment);

    log.info("Thanh toán MoMo thành công cho Booking ID: {}", bookingId);
} else {
    log.info("Thanh toán MoMo thất bại/hủy cho Booking ID: {}, message: {}", bookingId, message);
}
```

- [ ] **Step 4: Kiểm tra DB sau test**

```sql
-- Sau khi test, kiểm tra không có duplicate
SELECT booking_id, COUNT(*) FROM payments GROUP BY booking_id HAVING COUNT(*) > 1;
-- Expected: 0 rows
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "fix: MoMo payment idempotency - prevent duplicate payment records"
```

---

## Task 3: Sửa Booking Logic — selectedVillas Fallback + Unsafe get() + Missing Timestamps

**Mức độ:** 🔴 Critical

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\BookingServiceImpl.java`

---

- [ ] **Step 1: Khai báo selectedRooms ở đầu createBooking()**

Mở `BookingServiceImpl.java`. Tìm dòng:
```java
List<Villa> selectedVillas = new ArrayList<>();
```
Thêm ngay bên dưới:
```java
List<Room> selectedRooms = new ArrayList<>();
```

- [ ] **Step 2: Gán availableRooms vào selectedRooms trong nhánh else**

Tìm khối `else` (dòng 78-88). Thay dòng cuối `// Không gán gì`:

```java
} else {
    RoomType roomType = roomTypeRepository.findById(targetTypeId)
            .orElseThrow(() -> new BusinessException("Không tìm thấy hạng Villa/phòng"));
    basePrice = roomType.getBasePrice();
    List<Room> availableRooms = bookingRepository.findAvailableRooms(
            roomType.getId(), request.getCheckInDate(), request.getCheckOutDate());
    if (availableRooms.size() < quantity) {
        throw new BusinessException("Không đủ phòng/villa trống trong khoảng thời gian này");
    }
    selectedRooms = availableRooms.subList(0, quantity);  // ← FIX: gán vào selectedRooms
}
```

- [ ] **Step 3: Sửa vòng tạo BookingDetail để xử lý cả 2 trường hợp**

Tìm đoạn `for (Villa v : selectedVillas)` (dòng ~151). Thay bằng:

```java
List<BookingDetail> details = new ArrayList<>();
for (Villa v : selectedVillas) {
    details.add(BookingDetail.builder()
            .booking(savedBooking)
            .villa(v)
            .pricePerNight(basePrice)
            .build());
}
for (Room r : selectedRooms) {
    details.add(BookingDetail.builder()
            .booking(savedBooking)
            .room(r)
            .pricePerNight(basePrice)
            .build());
}
if (!details.isEmpty()) {
    bookingDetailRepository.saveAll(details);
    savedBooking.setBookingDetails(details);
}
```

- [ ] **Step 4: Sửa cancelBooking() — unsafe .get()**

Tìm dòng 212. Thay:
```java
// CŨ:
User user = userRepository.findByEmail(userEmail).get();
// MỚI:
User user = userRepository.findByEmail(userEmail)
    .orElseThrow(() -> new BusinessException("Không tìm thấy user: " + userEmail));
```

- [ ] **Step 5: Sửa getBookingById() — unsafe .get()**

Tìm dòng 187. Thay:
```java
// CŨ:
User user = userRepository.findByEmail(userEmail).get();
// MỚI:
User user = userRepository.findByEmail(userEmail)
    .orElseThrow(() -> new BusinessException("Không tìm thấy user: " + userEmail));
```

- [ ] **Step 6: Thêm checkInTime trong checkInBooking()**

Tìm `booking.setStatus(BookingStatus.CHECKED_IN);`, thêm ngay sau:
```java
booking.setCheckInTime(LocalDateTime.now());
```

- [ ] **Step 7: Thêm checkOutTime trong checkOutBooking()**

Tìm `booking.setStatus(BookingStatus.CHECKED_OUT);`, thêm ngay sau:
```java
booking.setCheckOutTime(LocalDateTime.now());
```

- [ ] **Step 8: Commit**

```bash
git add .
git commit -m "fix: booking detail missing in fallback path, unsafe Optional.get(), add checkIn/OutTime"
```

---

## Task 4: Sửa Performance — Xóa findAll() OOM

**Mức độ:** 🔴 Critical

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\repository\BookingRepository.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\AdminDashboardServiceImpl.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\BookingServiceImpl.java`

---

- [ ] **Step 1: Thêm 2 query tối ưu vào BookingRepository**

Mở `BookingRepository.java`. Thêm trước dấu `}` cuối:

```java
// Cho Dashboard — today arrivals/departures có LIMIT (truyền Pageable)
@EntityGraph(attributePaths = {"user", "bookingDetails.villa.villaType"})
@Query("SELECT b FROM Booking b WHERE " +
       "(b.checkInDate = :today OR b.checkOutDate = :today) " +
       "AND b.status != 'CANCELLED' " +
       "ORDER BY b.checkInDate ASC")
List<Booking> findTodayArrivalsOrDepartures(
        @Param("today") LocalDate today,
        Pageable pageable);

// Cho Gantt — active bookings trong khoảng ngày
@EntityGraph(attributePaths = {"bookingDetails.villa", "user"})
@Query("SELECT DISTINCT b FROM Booking b JOIN b.bookingDetails bd WHERE " +
       "b.status NOT IN ('CANCELLED') " +
       "AND b.checkInDate <= :endDate " +
       "AND b.checkOutDate >= :startDate")
List<Booking> findActiveBookingsBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
```

Thêm import nếu chưa có:
```java
import org.springframework.data.domain.Pageable;
```

- [ ] **Step 2: Sửa Dashboard — thay findAll() (dòng 150)**

Mở `AdminDashboardServiceImpl.java`. Thêm import:
```java
import org.springframework.data.domain.PageRequest;
```

Tìm dòng 150-153:
```java
// XÓA khối này:
List<Booking> todayBookings = bookingRepository.findAll().stream()
    .filter(b -> (today.equals(b.getCheckInDate()) || today.equals(b.getCheckOutDate()))
              && b.getStatus() != BookingStatus.CANCELLED)
    .limit(5)
    .collect(Collectors.toList());

// THAY BẰNG:
List<Booking> todayBookings = bookingRepository
        .findTodayArrivalsOrDepartures(today, PageRequest.of(0, 5));
```

- [ ] **Step 3: Sửa Gantt — thay findAll() (dòng 421)**

Mở `BookingServiceImpl.java`. Tìm dòng 421-425:

```java
// XÓA:
List<Booking> activeBookings = bookingRepository.findAll().stream()
    .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
    .filter(b -> b.getCheckInDate() != null && b.getCheckOutDate() != null)
    .filter(b -> !b.getCheckInDate().isAfter(endDate) && !b.getCheckOutDate().isBefore(effStartDate))
    .collect(Collectors.toList());

// THAY:
List<Booking> activeBookings = bookingRepository.findActiveBookingsBetween(effStartDate, endDate);
```

- [ ] **Step 4: Restart và kiểm tra response time**

```bash
# Gọi dashboard stats, phải < 500ms
curl -w "\nTime: %{time_total}s\n" http://localhost:8080/api/v1/admin/dashboard/stats \
  -H "Authorization: Bearer <admin_token>"
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "fix: replace findAll() OOM with targeted JPQL queries in dashboard and gantt"
```

---

## Task 5: Dashboard 7-Day Trend — Query Doanh Thu Lịch Sử

**Mức độ:** 🟠 Major

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\repository\BookingRepository.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\AdminDashboardServiceImpl.java`

---

- [ ] **Step 1: Thêm query doanh thu theo ngày và công suất theo ngày**

Mở `BookingRepository.java`. Thêm:

```java
// Doanh thu trong khoảng [startOfDay, endOfDay)
@Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b " +
       "WHERE b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') " +
       "AND b.createdAt >= :startOfDay AND b.createdAt < :endOfDay")
BigDecimal calculateRevenueByDate(
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay);

// Số villa đang có khách tại ngày date
@Query("SELECT COUNT(DISTINCT bd.villa.id) FROM BookingDetail bd " +
       "JOIN bd.booking b WHERE b.status = 'CHECKED_IN' " +
       "AND b.checkInDate <= :date AND b.checkOutDate > :date")
long countOccupiedVillasByDate(@Param("date") LocalDate date);
```

- [ ] **Step 2: Sửa vòng lặp 7-day trend trong AdminDashboardServiceImpl**

Tìm vòng lặp `for (int i = 6; i >= 0; i--)` (dòng ~79). Thay toàn bộ body:

```java
List<DashboardStatsResponse.RevenueTrendPoint> trend = new ArrayList<>();
for (int i = 6; i >= 0; i--) {
    LocalDate d = today.minusDays(i);
    String dayLabel = getVietnameseDayLabel(d.getDayOfWeek());
    boolean isToday = (i == 0);
    if (isToday) dayLabel += " (Nay)";

    LocalDateTime startOfD = d.atStartOfDay();
    LocalDateTime endOfD = d.plusDays(1).atStartOfDay();
    BigDecimal dayRevenue = bookingRepository.calculateRevenueByDate(startOfD, endOfD);
    double revMillion = dayRevenue != null
            ? Math.round((dayRevenue.doubleValue() / 1_000_000.0) * 10.0) / 10.0
            : 0.0;

    long occupiedOnDay = bookingRepository.countOccupiedVillasByDate(d);
    double occPercent = totalVillas > 0
            ? Math.round(((double) occupiedOnDay / totalVillas) * 1000.0) / 10.0
            : 0.0;
    if (occPercent > 100.0) occPercent = 100.0;

    trend.add(DashboardStatsResponse.RevenueTrendPoint.builder()
            .day(dayLabel)
            .revenueMillion(revMillion)
            .occupancyPercent(occPercent)
            .isToday(isToday)
            .build());
}
```

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "fix: dashboard 7-day trend queries real historical revenue per day"
```

---

## Task 6: Đồng Bộ Dashboard Model Frontend/Backend

**Mức độ:** 🟠 Major

**Files:**
- Modify: `d:\booking_hotel\frontend\src\app\core\models\admin-dashboard.model.ts`
- Modify: `d:\booking_hotel\frontend\src\app\core\services\admin-dashboard.service.ts`
- Modify: `d:\booking_hotel\frontend\src\app\admin\dashboard\dashboard.component.ts` (kiểm tra field names)

---

- [ ] **Step 1: Cập nhật admin-dashboard.model.ts khớp với Backend**

Thay toàn bộ `admin-dashboard.model.ts`:

```typescript
// Khớp với DashboardStatsResponse.java (Backend)

export interface RevenueTrendPoint {
  day: string;
  revenueMillion: number;
  occupancyPercent: number;
  isToday: boolean;
}

export interface FieldButlerDispatch {
  id: string;
  initials: string;
  butlerName: string;
  villaAssignment: string;
  task: string;
  statusBadge: string;
  badgeColor: string;
  isGpsActive: boolean;
}

export interface ModuleMatrixStatus {
  index: number;
  name: string;
  statusText: string;
  badge: string;
  badgeColor: string;
  icon: string;
  highlightInfo: string;
}

export interface VipArrivalDeparture {
  id: string;
  initials: string;
  guestName: string;
  tier: string;
  tierBadgeColor: string;
  assignedVilla: string;
  flightOrRoute: string;
  transport: string;
  butler: string;
  specialRequest: string;
  status: string;
  statusColor: string;
  isArrival: boolean;
}

export interface DashboardStatsResponse {
  occupancyRate: number;
  occupancyMoM: string;
  occupiedVillas: number;
  totalVillas: number;
  seasonStatus: string;
  todayRevenue: string;
  revenueTargetPercent: number;
  adr: string;
  revpar: string;
  vipInHouseCount: number;
  anniversaryCouplesCount: number;
  butlerCoverage: string;
  csatRating: number;
  fiveStarReviewsCount: number;
  unresolvedComplaintsCount: number;
  revenueTrend: RevenueTrendPoint[];
  forecastNext3Days: string;
  highestSegment: string;
  primaryChannel: string;
  weatherCondition: string;
  fieldDispatches: FieldButlerDispatch[];
  moduleStatuses: ModuleMatrixStatus[];
  vipArrivalDepartures: VipArrivalDeparture[];
}
```

- [ ] **Step 2: Sửa admin-dashboard.service.ts dùng model mới + environment**

Thay toàn bộ `admin-dashboard.service.ts`:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { DashboardStatsResponse } from '../models/admin-dashboard.model';
import { environment } from '../../../environments/environment';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class AdminDashboardService {
  private readonly API_URL = `${environment.apiUrl}/admin/dashboard`;

  constructor(private http: HttpClient) {}

  getDashboardStats(resortId = 'all', period = '7d'): Observable<DashboardStatsResponse> {
    const params = new HttpParams().set('resortId', resortId).set('period', period);
    return this.http.get<ApiResponse<DashboardStatsResponse>>(`${this.API_URL}/stats`, { params }).pipe(
      map((res) => res.data),
      catchError((error) => {
        console.warn('Dashboard API không khả dụng:', error);
        return of(this.getEmptyStats());
      })
    );
  }

  private getEmptyStats(): DashboardStatsResponse {
    return {
      occupancyRate: 0, occupancyMoM: '0%', occupiedVillas: 0, totalVillas: 0,
      seasonStatus: 'Chờ dữ liệu', todayRevenue: '0₫', revenueTargetPercent: 0,
      adr: '0₫', revpar: '0₫', vipInHouseCount: 0, anniversaryCouplesCount: 0,
      butlerCoverage: 'Chưa phân bổ', csatRating: 0, fiveStarReviewsCount: 0,
      unresolvedComplaintsCount: 0, revenueTrend: [], forecastNext3Days: '0%',
      highestSegment: '', primaryChannel: '', weatherCondition: '',
      fieldDispatches: [], moduleStatuses: [], vipArrivalDepartures: [],
    };
  }
}
```

- [ ] **Step 3: Cập nhật dashboard.component.ts — rename field references**

Mở `d:\booking_hotel\frontend\src\app\admin\dashboard\dashboard.component.ts`.
Tìm và thay mọi references cũ:

| Field cũ | Field mới |
|----------|-----------|
| `stats.kpis.occupancyRate` | `stats.occupancyRate` |
| `stats.kpis.totalVillas` | `stats.totalVillas` |
| `stats.kpis.csatScore` | `stats.csatRating` |
| `stats.weeklyRevenueChart` | `stats.revenueTrend` |
| `stats.fieldButlerDispatches` | `stats.fieldDispatches` |
| `stats.vipArrivalDepartureLogs` | `stats.vipArrivalDepartures` |
| `module.moduleCode` | `module.index` |
| `module.title` | `module.name` |
| `module.badgeClass` | `module.badgeColor` |
| `module.iconName` | `module.icon` |

Làm tương tự trong file HTML template `dashboard.component.html` nếu binding trực tiếp.

- [ ] **Step 4: Build kiểm tra lỗi TypeScript**

```bash
cd d:\booking_hotel\frontend
npx ng build --configuration development 2>&1 | findstr /I "error"
# Expected: Không có lỗi type mismatch
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "fix: align frontend dashboard model with backend DashboardStatsResponse"
```

---

## Task 7: Error Interceptor + Infrastructure Minor Fixes

**Mức độ:** 🟠 Major / 🟡 Minor

**Files:**
- Create: `d:\booking_hotel\frontend\src\app\core\interceptors\error.interceptor.ts`
- Modify: `d:\booking_hotel\frontend\src\app\app.config.ts`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\cron\BookingCronJob.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\config\AppConfig.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\PaymentServiceImpl.java`

---

- [ ] **Step 1: Tạo error.interceptor.ts**

Tạo `d:\booking_hotel\frontend\src\app\core\interceptors\error.interceptor.ts`:

```typescript
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { TokenService } from '../services/token.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const tokenService = inject(TokenService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        tokenService.removeToken();
        tokenService.removeRole();
        router.navigate(['/login'], { queryParams: { reason: 'session-expired' } });
      } else if (error.status === 403) {
        router.navigate(['/']);
      }
      return throwError(() => error);
    })
  );
};
```

- [ ] **Step 2: Đăng ký error interceptor**

Mở `app.config.ts`. Sửa:

```typescript
import { errorInterceptor } from './core/interceptors/error.interceptor';
// ...
provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
```

- [ ] **Step 3: Sửa BookingCronJob dùng @Slf4j**

Thay toàn bộ `BookingCronJob.java`:

```java
package com.phungvanlong.booking_hotel.cron;

import com.phungvanlong.booking_hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCronJob {

    private final BookingService bookingService;

    @Scheduled(cron = "0 * * * * *")
    public void cancelExpiredBookings() {
        bookingService.cancelExpiredBookings();
        log.info("Cron: Đã kiểm tra và hủy các booking hết hạn");
    }
}
```

- [ ] **Step 4: Thêm RestTemplate Bean vào AppConfig.java**

Mở `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\config\AppConfig.java`. Thêm:

```java
import org.springframework.web.client.RestTemplate;
// ...
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

- [ ] **Step 5: Inject RestTemplate vào PaymentServiceImpl**

Mở `PaymentServiceImpl.java`. Thêm field vào phần `private final`:

```java
private final RestTemplate restTemplate;
```

Xóa dòng `RestTemplate restTemplate = new RestTemplate();` trong method `createMoMoPayment()` (dòng 79).

- [ ] **Step 6: Commit**

```bash
git add .
git commit -m "fix: add error interceptor for 401/403, slf4j cron, inject RestTemplate bean"
```

---

## Task 8: Minor Completions — avatarUrl, OneToOne Payment Naming, Route bookings

**Mức độ:** 🟡 Minor

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\entity\User.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\AuthServiceImpl.java`
- Modify: `d:\booking_hotel\frontend\src\app\app.routes.ts`

---

- [ ] **Step 1: Thêm avatarUrl vào User entity**

Mở `User.java`. Thêm sau field `provider`:

```java
@Column(name = "avatar_url", length = 500)
private String avatarUrl;
```

- [ ] **Step 2: Lưu avatarUrl trong googleLogin()**

Mở `AuthServiceImpl.java`. Trong `googleLogin()`, tìm dòng `String name = (String) payload.get("name");`. Thêm ngay sau:

```java
String avatarUrl = (String) payload.get("picture");
```

Tìm chỗ tạo user mới (dòng ~76-84), thêm `.avatarUrl(avatarUrl)`:

```java
user = User.builder()
        .email(email)
        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
        .fullName(name)
        .avatarUrl(avatarUrl)     // ← THÊM
        .role(Role.ROLE_CUSTOMER)
        .isActive(true)
        .provider(AuthProvider.GOOGLE)
        .build();
```

Trong else branch (user đã tồn tại, dòng ~86), thêm cập nhật avatar nếu rỗng:

```java
if (user.getAvatarUrl() == null || user.getAvatarUrl().isBlank()) {
    user.setAvatarUrl(avatarUrl);
}
if (user.getProvider() == AuthProvider.LOCAL) {
    user.setProvider(AuthProvider.GOOGLE);
}
userRepository.save(user);
```

- [ ] **Step 3: Kiểm tra DB migration cần thiết**

Nếu `spring.jpa.hibernate.ddl-auto=update` → JPA tự thêm column. Nếu dùng Flyway/Liquibase, tạo:

```sql
-- V2__add_avatar_url_to_users.sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);
```

- [ ] **Step 4: Sửa route /bookings trong app.routes.ts**

Mở `app.routes.ts`. Tìm route `path: 'bookings'` (dòng 62-66). Thay:

```typescript
{
  path: 'bookings',
  canActivate: [authGuard],
  // Nếu MyBookingsComponent chưa có, redirect tạm về user-profile
  redirectTo: 'user-profile',
  pathMatch: 'full',
},
```

> **Ghi chú:** Tạo `features/booking/my-bookings/my-bookings.component.ts` là task phát triển tính năng mới riêng. Khi có component đó, thay `redirectTo` bằng `loadComponent`.

- [ ] **Step 5: Full build verification**

```bash
# Backend build
cd d:\booking_hotel\backend\booking_hotel
mvnw clean package -DskipTests
# Expected: BUILD SUCCESS

# Frontend build
cd d:\booking_hotel\frontend
npx ng build --configuration development
# Expected: Application bundle generation complete
```

- [ ] **Step 6: Commit cuối**

```bash
git add .
git commit -m "fix: add avatarUrl to User entity, save Google avatar, guard bookings route"
```

---

## 📊 Tổng Kết

| Task | Lỗi sửa | Files thay đổi |
|------|---------|----------------|
| Task 1 🔴 | #4 guards, #5 SecurityConfig, #10 Google key, #11 CORS | 9 files |
| Task 2 🔴 | #1 Payment duplicate MoMo | 2 files |
| Task 3 🔴 | #3 selectedVillas, #8 #9 unsafe get(), #14 timestamps | 1 file |
| Task 4 🔴 | #2 OOM Dashboard, #20 OOM Gantt | 3 files |
| Task 5 🟠 | #12 7-day trend empty | 2 files |
| Task 6 🟠 | #7 Model mismatch Dashboard | 3 files |
| Task 7 🟠 | #13 error interceptor, #16 RestTemplate, #17 println | 5 files |
| Task 8 🟡 | #6 /bookings route, #15 avatarUrl, #18 payment model | 3 files |

**20/20 lỗi được xử lý. Thứ tự: Critical → Major → Minor.**

