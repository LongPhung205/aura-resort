# Thiết kế Kiến trúc: Quản lý Phân khu (Zone Management)

## 1. Mục tiêu & Bối cảnh
- Hiện tại CSDL dự án chưa có bảng `zones` độc lập để quản lý danh mục phân khu nghỉ dưỡng. Bảng `villas` và `rooms` chỉ lưu cột `zone` kiểu `VARCHAR(50)` tự do.
- Ở Frontend, danh sách phân khu cấu hình (icon, tag, màu badge, mô tả) đang được lưu tạm trong `localStorage`, dẫn đến rủi ro mất dữ liệu khi đổi trình duyệt hoặc xóa cache.
- Mục tiêu: Chuẩn hóa kiến trúc CSDL quan hệ với Entity `Zone` (bảng `zones`), thiết lập quan hệ `@ManyToOne` từ `Villa` và `Room` tới `Zone`, xây dựng bộ REST API CRUD cho Admin và đồng bộ dữ liệu thực thể từ Backend lên Frontend, loại bỏ hoàn toàn `localStorage`.

---

## 2. Thiết kế Cơ sở dữ liệu & Entity Model

### 2.1. Entity `Zone.java` (Bảng `zones`)
* **Package**: `com.phungvanlong.booking_hotel.entity`
* **Kế thừa**: `BaseEntity` (có sẵn `createdAt`, `updatedAt`)
* **Các trường**:
  * `id`: `Long`, Khóa chính, `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
  * `name`: `String`, `@Column(nullable = false, unique = true, length = 100)` - Tên phân khu (ví dụ: "Ngọc Trai", "San Hô").
  * `matchKey`: `String`, `@Column(name = "match_key", length = 100)` - Dùng tìm kiếm/so khớp lowercase không dấu (ví dụ: "ngoc trai").
  * `tag`: `String`, `@Column(length = 50)` - Nhãn hiển thị viết hoa (ví dụ: "NGỌC TRAI").
  * `icon`: `String`, `@Column(length = 50)` - Tên Google Material Icon (ví dụ: "holiday_village", "waves").
  * `badgeClass`: `String`, `@Column(name = "badge_class", length = 100)` - Chuỗi CSS class cho badge (ví dụ: "bg-amber-50 text-amber-700 border-amber-200").
  * `description`: `String`, `@Column(columnDefinition = "TEXT")` - Mô tả phân khu.
  * `isActive`: `Boolean`, `@Builder.Default private Boolean isActive = true;` - Trạng thái hoạt động.

### 2.2. Khóa ngoại liên kết trong `Villa.java` & `Room.java`
* **Trong `Villa.java`**:
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "zone_id")
  private Zone zone;
  ```
* **Trong `Room.java`**:
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "zone_id")
  private Zone zone;
  ```

---

## 3. Thiết kế DTO & Backend Services

### 3.1. DTOs
* **`ZoneRequest.java`** trong `com.phungvanlong.booking_hotel.dto.request`:
  * `name`: `@NotBlank(message = "Tên phân khu không được để trống")`
  * `tag`: `String`
  * `icon`: `String`
  * `badgeClass`: `String`
  * `description`: `String`
  * `isActive`: `Boolean`
* **`ZoneResponse.java`** trong `com.phungvanlong.booking_hotel.dto.response`:
  * `id`: `Long`
  * `name`: `String`
  * `matchKey`: `String`
  * `tag`: `String`
  * `icon`: `String`
  * `badgeClass`: `String`
  * `description`: `String`
  * `isActive`: `Boolean`
  * `villaCount`: `long` (Tổng số Villa thuộc phân khu này)
  * `createdAt`: `LocalDateTime`
* **Cập nhật `VillaRequest.java`**:
  * Thêm `private Long zoneId;`
  * Giữ `private String zone;` (đảm bảo tương thích nếu client cũ gửi chuỗi)
* **Cập nhật `VillaResponse.java`**:
  * Thêm `private Long zoneId;`
  * Giữ `private String zone;` (trả về tên phân khu `villa.getZone() != null ? villa.getZone().getName() : null`)

### 3.2. Repositories
* **`ZoneRepository.java`**:
  * `Optional<Zone> findByNameIgnoreCase(String name);`
  * `boolean existsByNameIgnoreCase(String name);`
  * `boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);`
  * `List<Zone> findAllByOrderByCreatedAtAsc();`
* **Cập nhật `VillaRepository.java`**:
  * `long countByZoneId(Long zoneId);`
  * `List<Villa> findByZoneId(Long zoneId);`
  * Cập nhật `@Query` `findByFilters`:
    ```java
    @Query("SELECT v FROM Villa v WHERE " +
           "(:typeId IS NULL OR v.villaType.id = :typeId) AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:zone IS NULL OR (v.zone IS NOT NULL AND v.zone.name = :zone))")
    ```

### 3.3. Service & Controller
* **`AdminZoneService.java` / `AdminZoneServiceImpl.java`**:
  * `List<ZoneResponse> getAllZones();`
  * `ZoneResponse getZoneById(Long id);`
  * `ZoneResponse createZone(ZoneRequest request);`
  * `ZoneResponse updateZone(Long id, ZoneRequest request);`
  * `void deleteZone(Long id);` (Kiểm tra nếu `villaRepository.countByZoneId(id) > 0` thì ném `BusinessException("Không thể xóa phân khu vì đang có biệt thự trực thuộc")`).
* **`AdminZoneController.java`**:
  * `@RestController @RequestMapping("/admin/zones")`
  * Cung cấp các endpoints:
    * `GET /api/v1/admin/zones`
    * `GET /api/v1/admin/zones/{id}`
    * `POST /api/v1/admin/zones`
    * `PUT /api/v1/admin/zones/{id}`
    * `DELETE /api/v1/admin/zones/{id}`

---

## 4. Đồng bộ Frontend

### 4.1. Service (`admin-housekeeping.service.ts`)
* Định nghĩa interface `ZoneItem`:
  ```typescript
  export interface ZoneItem {
    id: number;
    name: string;
    matchKey: string;
    tag: string;
    icon: string;
    badgeClass: string;
    description?: string;
    isActive?: boolean;
    villaCount?: number;
  }
  ```
* Bổ sung các phương thức HTTP:
  * `getZones(): Observable<ZoneItem[]>`
  * `createZone(payload: Partial<ZoneItem>): Observable<ZoneItem>`
  * `updateZone(id: number, payload: Partial<ZoneItem>): Observable<ZoneItem>`
  * `deleteZone(id: number): Observable<void>`

### 4.2. Quản lý phòng (`room-management.component.ts`)
* Xóa bỏ hoàn toàn code đọc/ghi `localStorage` (`aura_resort_custom_zones`).
* Hàm `initCustomZones()` gọi `adminHousekeepingService.getZones()`.
* Khi tạo mới/sửa/xóa phân khu trong Modal, gọi API tương ứng của `adminHousekeepingService` và cập nhật danh sách hiển thị trực tiếp.

---

## 5. Quy trình Kiểm thử & Xác minh
* Biên dịch Backend với `./mvnw.cmd clean compile -DskipTests` -> Đảm bảo không có lỗi cú pháp, compile thành công.
* Chạy unit tests hiện có (`VillaDtoTest`, `VillaServiceRedesignTest`) để đảm bảo không gãy logic nghiệp vụ.
* Kiểm tra TypeScript build ở Frontend (`ng build` hoặc `ng serve`) đảm bảo type safety.
