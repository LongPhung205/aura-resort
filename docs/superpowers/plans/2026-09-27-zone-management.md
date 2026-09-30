# Kế hoạch Triển khai Quản lý Phân khu (Zone Management)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Triển khai bảng danh mục `zones` trong CSDL, thiết lập liên kết quan hệ `@ManyToOne` từ `Villa` và `Room` tới `Zone`, xây dựng trọn bộ REST API CRUD và đồng bộ Frontend loại bỏ `localStorage`.

**Architecture:** Sử dụng Spring Data JPA để tạo bảng `zones` kế thừa `BaseEntity` và thiết lập khóa ngoại `zone_id` trong `villas` và `rooms`. Xây dựng tầng Service và Controller với chuẩn REST API (`ApiResponse`, `ZoneRequest`, `ZoneResponse`). Tại Frontend, bổ sung các phương thức gọi HTTP trong `AdminHousekeepingService` và kết nối với `room-management.component.ts`.

**Tech Stack:** Java 17, Spring Boot 3, Spring Data JPA, MySQL/Hibernate, Lombok, Angular 17+ / TypeScript.

## Global Constraints
- Không tạo Data Initializer tự động seed trước "Ngọc Trai" và "San Hô" (dữ liệu do người dùng tạo thủ công).
- `VillaResponse` và `RoomResponse` phải giữ trường `zone` (String name) song song với `zoneId` để đảm bảo 100% tương thích ngược với các logic hiện hữu.
- Các Request DTO đặt trong `com.phungvanlong.booking_hotel.dto.request`, Response DTO đặt trong `com.phungvanlong.booking_hotel.dto.response`.
- Kiểm tra tính toàn vẹn dữ liệu: Không cho phép xóa phân khu nếu đang có Villa/Room tham chiếu tới.

---

### Task 1: Tạo Entity `Zone` và `ZoneRepository`

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Zone.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/ZoneRepository.java`

**Interfaces:**
- Produces:
  - `Zone` entity với các trường `id`, `name`, `matchKey`, `tag`, `icon`, `badgeClass`, `description`, `isActive`.
  - `ZoneRepository` kế thừa `JpaRepository<Zone, Long>`.

- [ ] **Step 1: Tạo Entity `Zone.java`**
Tạo file `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Zone.java`:
```java
package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "match_key", length = 100)
    private String matchKey;

    @Column(length = 50)
    private String tag;

    @Column(length = 50)
    private String icon;

    @Column(name = "badge_class", length = 100)
    private String badgeClass;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
```

- [ ] **Step 2: Tạo `ZoneRepository.java`**
Tạo file `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/ZoneRepository.java`:
```java
package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Zone> findAllByOrderByCreatedAtAsc();
}
```

- [ ] **Step 3: Biên dịch kiểm tra**
Chạy: `.\mvnw.cmd compile -DskipTests` tại thư mục `backend/booking_hotel`
Kỳ vọng: BUILD SUCCESS.

---

### Task 2: Cập nhật `Villa` & `Room` Entities và `VillaRepository`

**Files:**
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Villa.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Room.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/VillaRepository.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/VillaResponse.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/RoomResponse.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/VillaRequest.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/RoomRequest.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/HousekeepingTaskResponse.java`

- [ ] **Step 1: Cập nhật `Villa.java` và `Room.java`**
Thay thế `private String zone;` thành `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "zone_id") private Zone zone;`.

- [ ] **Step 2: Cập nhật `VillaRepository.java`**
Bổ sung `countByZoneId(Long zoneId)` và sửa `@Query findByFilters`:
```java
    long countByZoneId(Long zoneId);

    List<Villa> findByZoneId(Long zoneId);

    @Query("SELECT v FROM Villa v WHERE " +
           "(:typeId IS NULL OR v.villaType.id = :typeId) AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:zone IS NULL OR (v.zone IS NOT NULL AND v.zone.name = :zone))")
    List<Villa> findByFilters(
            @Param("typeId") Long typeId,
            @Param("status") VillaStatus status,
            @Param("zone") String zone);
```

- [ ] **Step 3: Cập nhật DTOs (`VillaResponse`, `RoomResponse`, `VillaRequest`, `RoomRequest`, `HousekeepingTaskResponse`)**
  - Trong `VillaResponse.fromEntity`: ánh xạ `zoneId = villa.getZone() != null ? villa.getZone().getId() : null` và `zone = villa.getZone() != null ? villa.getZone().getName() : null`.
  - Trong `RoomResponse.fromEntity`: ánh xạ `zoneId` và `zone` tương tự.
  - Trong `HousekeepingTaskResponse`: lấy tên phân khu `task.getVilla().getZone() != null ? task.getVilla().getZone().getName() : null`.
  - Trong `VillaRequest` & `RoomRequest`: thêm trường `private Long zoneId;`.

- [ ] **Step 4: Biên dịch kiểm tra**
Chạy: `.\mvnw.cmd compile -DskipTests` tại thư mục `backend/booking_hotel`
Kỳ vọng: BUILD SUCCESS.

---

### Task 3: Tạo DTOs và Service Quản lý Phân khu (`AdminZoneService`)

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/ZoneRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/ZoneResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/AdminZoneService.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/AdminZoneServiceImpl.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/VillaServiceImpl.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/RoomServiceImpl.java`

- [ ] **Step 1: Tạo `ZoneRequest.java`**
```java
package com.phungvanlong.booking_hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneRequest {

    @NotBlank(message = "Tên phân khu không được để trống")
    private String name;

    private String tag;
    private String icon;
    private String badgeClass;
    private String description;
    private Boolean isActive;
}
```

- [ ] **Step 2: Tạo `ZoneResponse.java`**
```java
package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneResponse {

    private Long id;
    private String name;
    private String matchKey;
    private String tag;
    private String icon;
    private String badgeClass;
    private String description;
    private Boolean isActive;
    private long villaCount;
    private LocalDateTime createdAt;

    public static ZoneResponse fromEntity(Zone entity, long villaCount) {
        return ZoneResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .matchKey(entity.getMatchKey())
                .tag(entity.getTag())
                .icon(entity.getIcon())
                .badgeClass(entity.getBadgeClass())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .villaCount(villaCount)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
```

- [ ] **Step 3: Tạo `AdminZoneService.java` interface**
```java
package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ZoneRequest;
import com.phungvanlong.booking_hotel.dto.response.ZoneResponse;

import java.util.List;

public interface AdminZoneService {
    List<ZoneResponse> getAllZones();
    ZoneResponse getZoneById(Long id);
    ZoneResponse createZone(ZoneRequest request);
    ZoneResponse updateZone(Long id, ZoneRequest request);
    void deleteZone(Long id);
}
```

- [ ] **Step 4: Cài đặt `AdminZoneServiceImpl.java`**
Thực hiện các phương thức:
- `getAllZones()`: Lấy tất cả `Zone`, kèm `villaRepository.countByZoneId(z.getId())`.
- `createZone()`: Validate trùng tên qua `zoneRepository.existsByNameIgnoreCase(name)`.
- `updateZone()`: Validate trùng tên qua `zoneRepository.existsByNameIgnoreCaseAndIdNot(name, id)`.
- `deleteZone()`: Kiểm tra `villaRepository.countByZoneId(id) > 0` -> quăng `BusinessException("Không thể xóa phân khu vì đang có biệt thự trực thuộc!")`.

- [ ] **Step 5: Cập nhật `VillaServiceImpl.java` & `RoomServiceImpl.java`**
Trong logic tạo/sửa Villa và Room:
- Nếu `request.getZoneId() != null`, tìm `Zone` theo ID.
- Nếu không có ID nhưng có `request.getZone()` (tên chuỗi), tìm theo tên hoặc tự động tạo nếu chưa có.
- Gán entity `Zone` vào `villa.setZone(...)` và `room.setZone(...)`.

- [ ] **Step 6: Biên dịch kiểm tra**
Chạy: `.\mvnw.cmd compile -DskipTests` tại thư mục `backend/booking_hotel`
Kỳ vọng: BUILD SUCCESS.

---

### Task 4: Tạo REST Controller `AdminZoneController`

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminZoneController.java`

- [ ] **Step 1: Tạo `AdminZoneController.java`**
```java
package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.ZoneRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.ZoneResponse;
import com.phungvanlong.booking_hotel.service.AdminZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/zones")
@RequiredArgsConstructor
public class AdminZoneController {

    private final AdminZoneService adminZoneService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getAllZones() {
        List<ZoneResponse> zones = adminZoneService.getAllZones();
        return ResponseEntity.ok(ApiResponse.success(zones, "Lấy danh sách phân khu thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getZoneById(@PathVariable Long id) {
        ZoneResponse zone = adminZoneService.getZoneById(id);
        return ResponseEntity.ok(ApiResponse.success(zone, "Lấy thông tin phân khu thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ZoneResponse>> createZone(@Valid @RequestBody ZoneRequest request) {
        ZoneResponse created = adminZoneService.createZone(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Tạo phân khu mới thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> updateZone(
            @PathVariable Long id,
            @Valid @RequestBody ZoneRequest request) {
        ZoneResponse updated = adminZoneService.updateZone(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật phân khu thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteZone(@PathVariable Long id) {
        adminZoneService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa phân khu thành công"));
    }
}
```

- [ ] **Step 2: Biên dịch và chạy Unit Tests**
Chạy: `.\mvnw.cmd test` tại `backend/booking_hotel`
Kỳ vọng: Các test hiện hữu (`VillaDtoTest`, `VillaServiceRedesignTest`...) đều PASS.

---

### Task 5: Đồng bộ Frontend (`AdminHousekeepingService` & `RoomManagementComponent`)

**Files:**
- Modify: `frontend/src/app/core/services/admin-housekeeping.service.ts`
- Modify: `frontend/src/app/admin/room-management/room-management.component.ts`

- [ ] **Step 1: Bổ sung API Phân khu trong `admin-housekeeping.service.ts`**
Định nghĩa interface `ZoneItem` và thêm các phương thức:
```typescript
export interface ZoneItem {
  id?: number;
  name: string;
  matchKey?: string;
  tag?: string;
  icon?: string;
  badgeClass?: string;
  description?: string;
  isActive?: boolean;
  villaCount?: number;
}

getZones(): Observable<ZoneItem[]> {
  return this.http.get<ApiResponse<ZoneItem[]>>(`${this.apiUrl}/admin/zones`).pipe(
    map(res => res.data || [])
  );
}

createZone(payload: ZoneItem): Observable<ZoneItem> {
  return this.http.post<ApiResponse<ZoneItem>>(`${this.apiUrl}/admin/zones`, payload).pipe(
    map(res => res.data)
  );
}

updateZone(id: number, payload: ZoneItem): Observable<ZoneItem> {
  return this.http.put<ApiResponse<ZoneItem>>(`${this.apiUrl}/admin/zones/${id}`, payload).pipe(
    map(res => res.data)
  );
}

deleteZone(id: number): Observable<void> {
  return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/admin/zones/${id}`).pipe(
    map(() => void 0)
  );
}
```

- [ ] **Step 2: Cập nhật `room-management.component.ts`**
- Loại bỏ hoàn toàn `localStorage.getItem('aura_resort_custom_zones')` và `localStorage.setItem(...)`.
- Trong `initCustomZones()`, gọi `this.housekeepingService.getZones().subscribe(...)` để lấy dữ liệu thực tế từ Database.
- Trong `saveZone()`, gọi `createZone` hoặc `updateZone` qua Service, hiển thị Toast thông báo và reload danh sách.
- Trong `confirmDeleteZone()`, gọi `deleteZone(id)` qua Service và cập nhật state.

---

### Task 6: Kiểm thử Toàn diện & Xác nhận Hoàn tất

- [ ] **Step 1: Kiểm tra biên dịch Backend**
Chạy: `.\mvnw.cmd clean compile -DskipTests`
Kỳ vọng: BUILD SUCCESS.

- [ ] **Step 2: Chạy bộ kiểm thử Backend**
Chạy: `.\mvnw.cmd test`
Kỳ vọng: Tests PASS không có lỗi.

- [ ] **Step 3: Kiểm tra Frontend Build**
Xác nhận tiến trình `ng serve` trên frontend không có lỗi biên dịch TypeScript.
