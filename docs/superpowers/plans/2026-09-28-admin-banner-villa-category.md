# Quản Lý Banner Trang Chủ & Trang Phân Loại Villa (CMS) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Xây dựng hệ thống quản trị CMS cho phép Admin tạo/sửa/xóa/bật tắt Banner Slider trên Trang chủ, quản lý các Phân khu Biệt thự (Ngọc Trai, Sao Biển, San Hô...), đồng thời cung cấp giao diện Slider động trên trang chủ và Trang chi tiết phân loại Villa riêng biệt (`/villas/zone/:slug`) cho khách hàng.

**Architecture:** 
- **Backend (Spring Boot)**: Tạo thực thể `HomeBanner` và mở rộng `Zone` (thêm `slug`, `bannerUrl`, `highlights`, `displayOrder`). Cung cấp 2 tầng API: Public REST APIs cho Client (`/api/v1/public/banners`, `/api/v1/public/zones/{slug}`) và Admin REST APIs có xác thực (`/api/v1/admin/banners`, `/api/v1/admin/zones`).
- **Frontend (Angular)**: Bổ sung nhóm menu `NỘI DUNG & WEBSITE` trên Sidebar Admin; tạo trang quản lý Banner (`/admin/banners`) kèm Live Preview và trang quản lý Phân khu (`/admin/zones`). Nâng cấp `HeroSectionComponent` thành Slider tự động và tạo trang `ZoneDetailComponent` (`/villas/zone/:slug`).

**Tech Stack:** Java 17, Spring Boot 3, Spring Data JPA, Hibernate, MySQL, Angular 17+ Standalone Components, Tailwind CSS, Google Material Symbols.

## Global Constraints
- Tuân thủ cấu trúc RESTful API chuẩn (`/api/v1/public/...`, `/api/v1/admin/...`).
- Phải có dữ liệu mặc định (Fallback / Seed data) nếu CSDL chưa có banner nào để tránh trang chủ bị trống.
- Đảm bảo tương thích responsive 100% trên cả Desktop, Tablet và Mobile.
- Mã nguồn viết bằng TypeScript & Java, tuân thủ strict typing, không sử dụng `any` bừa bãi.

---

### Task 1: Backend Data Model - Entity `HomeBanner` & Nâng Cấp Entity `Zone`

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/HomeBanner.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Zone.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/HomeBannerRepository.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/ZoneRepository.java`

**Interfaces:**
- Produces: `HomeBanner` entity, `HomeBannerRepository` (`findAllByIsActiveTrueOrderByDisplayOrderAsc()`), `ZoneRepository.findBySlugIgnoreCase(String slug)`

- [ ] **Step 1: Tạo Entity `HomeBanner.java`**

```java
package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "home_banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeBanner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "mobile_image_url", length = 500)
    private String mobileImageUrl;

    @Column(name = "cta_text", length = 100)
    private String ctaText;

    @Column(name = "cta_link", length = 255)
    private String ctaLink;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "badges_json", columnDefinition = "TEXT")
    private String badgesJson;
}
```

- [ ] **Step 2: Nâng cấp Entity `Zone.java`**
Bổ sung các trường `slug`, `bannerUrl`, `highlights`, `displayOrder` vào `Zone.java`:
```java
    @Column(unique = true, length = 120)
    private String slug;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(columnDefinition = "TEXT")
    private String highlights;

    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 1;
```

- [ ] **Step 3: Tạo `HomeBannerRepository.java`**

```java
package com.phungvanlong.booking_hotel.repository;

import com.phungvanlong.booking_hotel.entity.HomeBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeBannerRepository extends JpaRepository<HomeBanner, Long> {
    List<HomeBanner> findAllByIsActiveTrueOrderByDisplayOrderAsc();
    List<HomeBanner> findAllByOrderByDisplayOrderAsc();
}
```

- [ ] **Step 4: Cập nhật `ZoneRepository.java`**
Bổ sung query tìm theo slug:
```java
    Optional<Zone> findBySlugIgnoreCase(String slug);
    boolean existsBySlugIgnoreCase(String slug);
```

- [ ] **Step 5: Kiểm tra biên dịch Backend**
Run: `./mvnw test-compile -DskipTests` (trong `backend/booking_hotel`)
Expected: `BUILD SUCCESS`

---

### Task 2: Backend DTOs & Services Cho Banner & Zone

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/HomeBannerRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/HomeBannerResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/ZoneDetailResponse.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/ZoneRequest.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/ZoneResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/HomeBannerService.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/HomeBannerServiceImpl.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/AdminZoneService.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/AdminZoneServiceImpl.java`

**Interfaces:**
- `HomeBannerService`:
  - `List<HomeBannerResponse> getActiveBanners()`
  - `List<HomeBannerResponse> getAllBanners()`
  - `HomeBannerResponse getBannerById(Long id)`
  - `HomeBannerResponse createBanner(HomeBannerRequest request)`
  - `HomeBannerResponse updateBanner(Long id, HomeBannerRequest request)`
  - `void toggleBannerStatus(Long id)`
  - `void deleteBanner(Long id)`
- `AdminZoneService`:
  - Bổ sung `ZoneDetailResponse getZoneDetailBySlug(String slug)`

- [ ] **Step 1: Tạo DTOs cho Banner và Zone Detail**
  - `HomeBannerRequest`: validation `@NotBlank` cho title và imageUrl.
  - `HomeBannerResponse`: mapper `fromEntity(HomeBanner banner)`.
  - `ZoneDetailResponse`: gồm thông tin phân khu (`ZoneResponse`) kèm `List<VillaResponse>` các căn biệt thự trực thuộc.
  - Cập nhật `ZoneRequest` & `ZoneResponse` thêm `slug`, `bannerUrl`, `highlights`, `displayOrder`.

- [ ] **Step 2: Viết `HomeBannerServiceImpl.java`**
  - Triển khai logic CRUD, đảm bảo `displayOrder` hợp lệ, hỗ trợ toggle active, tự động gán order kế tiếp khi tạo mới.

- [ ] **Step 3: Cập nhật `AdminZoneServiceImpl.java`**
  - Bổ sung logic tự động sinh `slug` nếu request không truyền slug (chuyển tiếng Việt có dấu sang không dấu `SlugUtils.toSlug(name)`).
  - Triển khai phương thức `getZoneDetailBySlug(String slug)` lấy thông tin phân khu và truy vấn danh sách Villa từ `VillaRepository.findByZoneId(zone.getId())`.

- [ ] **Step 4: Kiểm tra biên dịch Backend**
Run: `./mvnw test-compile -DskipTests`
Expected: `BUILD SUCCESS`

---

### Task 3: Backend REST Controllers (Public & Admin)

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/PublicBannerController.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminBannerController.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/PublicZoneController.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/SecurityConfig.java` (nếu cần cấp quyền truy cập cho `/api/v1/public/**`)

**Interfaces:**
- `PublicBannerController`: `GET /api/v1/public/banners`
- `PublicZoneController`: `GET /api/v1/public/zones`, `GET /api/v1/public/zones/{slug}`
- `AdminBannerController`: `GET/POST/PUT/DELETE /api/v1/admin/banners`
- `AdminZoneController`: Cập nhật xử lý các trường mới.

- [ ] **Step 1: Viết `PublicBannerController.java` & `PublicZoneController.java`**
  - Cho phép người dùng vãng lai truy cập không cần đăng nhập.
  - Trả về danh sách banners active và chi tiết phân khu theo slug.

- [ ] **Step 2: Viết `AdminBannerController.java`**
  - Cung cấp đầy đủ CRUD cho Ban quản lý.

- [ ] **Step 3: Kiểm tra cấu hình Spring Security**
  - Đảm bảo path `/api/v1/public/**` được `permitAll()`.

- [ ] **Step 4: Khởi tạo Seed Data Banners & Zones Mẫu**
  - Tạo `HomeBannerDataLoader` (hoặc chèn trong startup nếu bảng `home_banners` rỗng):
    - Slide 1: "Nâng Tầm Kỳ Nghỉ Đỉnh Cao", ảnh `/assets/images/rooms/grand-oceanfront.jpg`, subtitle "Hệ thống 12 điểm đến thượng lưu".
    - Slide 2: "Trải Nghiệm Biệt Thự Biển Sầm Sơn", ảnh `/assets/images/rooms/villa-beachfront.jpg`, subtitle "FLC Sầm Sơn Resort & Villas".
    - Slide 3: "Aura Elite Club VIP", ảnh `/assets/images/rooms/royal-penthouse.jpg`, subtitle "Đặc quyền nghỉ dưỡng sang trọng".
  - Đảm bảo 3 phân khu chuẩn có sẵn slug: `villa-ngoc-trai`, `villa-sao-bien`, `villa-san-ho`.

- [ ] **Step 5: Kiểm tra biên dịch Backend**
Run: `./mvnw test-compile -DskipTests`
Expected: `BUILD SUCCESS`

---

### Task 4: Frontend Models & Services

**Files:**
- Create: `frontend/src/app/core/models/banner.model.ts`
- Modify: `frontend/src/app/core/models/zone.model.ts`
- Create: `frontend/src/app/core/services/banner.service.ts`
- Create: `frontend/src/app/core/services/admin-banner.service.ts`
- Modify: `frontend/src/app/core/services/zone.service.ts`

**Interfaces:**
- `HomeBanner`: `{ id, title, subtitle, description, imageUrl, mobileImageUrl, ctaText, ctaLink, displayOrder, isActive }`
- `HomeBannerPayload`: interface tạo/sửa banner.
- `ZoneDetail`: `{ zone: Zone, villas: Villa[] }`

- [ ] **Step 1: Tạo `banner.model.ts` và cập nhật `zone.model.ts`**
  - Khai báo đầy đủ interface chuẩn TypeScript.

- [ ] **Step 2: Tạo `banner.service.ts` (Public) và `admin-banner.service.ts` (Admin)**
  - Phương thức: `getActiveBanners()`, `getAllBanners()`, `createBanner()`, `updateBanner()`, `toggleStatus()`, `deleteBanner()`.

- [ ] **Step 3: Cập nhật `zone.service.ts`**
  - Thêm phương thức `getZoneBySlug(slug: string): Observable<ZoneDetail>`.

- [ ] **Step 4: Kiểm tra build Frontend**
Run: `npm run build` (trong `frontend`)
Expected: `Application bundle generation complete`

---

### Task 5: Frontend Admin UI - Quản Lý Banner Trang Chủ

**Files:**
- Modify: `frontend/src/app/admin/layout/admin-layout.component.html`
- Create: `frontend/src/app/admin/banner-management/banner-management.component.ts`
- Create: `frontend/src/app/admin/banner-management/banner-management.component.html`
- Create: `frontend/src/app/admin/banner-management/banner-management.component.scss`
- Modify: `frontend/src/app/app.routes.ts` (đăng ký route `/admin/banners`)

- [ ] **Step 1: Cập nhật Sidebar Admin (`admin-layout.component.html`)**
  - Thêm khối `NỘI DUNG & WEBSITE`:
    - Menu `12. Banner Trang Chủ` (`routerLink="/admin/banners"`, icon `view_carousel`).
    - Menu `13. Phân Khu & Danh Mục` (`routerLink="/admin/zones"`, icon `holiday_village`).

- [ ] **Step 2: Tạo `BannerManagementComponent`**
  - Hiển thị danh sách banners dạng thẻ Grid sang trọng:
    - Ảnh thumbnail, tiêu đề, nút CTA, số thứ tự.
    - Switch Bật/Tắt hiển thị nhanh.
    - Nút Xem trước (Live Modal Preview).
    - Nút Sửa / Xóa.
  - Modal Thêm / Chỉnh sửa Banner:
    - Nhập tiêu đề, tiêu đề phụ, mô tả, nút bấm, link đích.
    - Chọn nhanh ảnh mẫu hoặc nhập URL ảnh.
    - Xem trước trực tiếp hình ảnh hiển thị trên mockup banner.

- [ ] **Step 3: Đăng ký route `/admin/banners` trong `app.routes.ts`**

- [ ] **Step 4: Kiểm tra build Frontend**
Run: `npm run build`
Expected: `Application bundle generation complete`

---

### Task 6: Frontend Admin UI - Quản Lý Phân Khu & Phân Loại Villa

**Files:**
- Create: `frontend/src/app/admin/zone-management/zone-management.component.ts`
- Create: `frontend/src/app/admin/zone-management/zone-management.component.html`
- Create: `frontend/src/app/admin/zone-management/zone-management.component.scss`
- Modify: `frontend/src/app/app.routes.ts` (đăng ký route `/admin/zones`)

- [ ] **Step 1: Tạo `ZoneManagementComponent`**
  - Hiển thị danh sách phân khu: **Ngọc Trai**, **Sao Biển**, **San Hô**...
  - Cho phép quản trị viên:
    - Cập nhật ảnh Banner lớn của từng phân khu.
    - Cập nhật Slug thân thiện (`villa-ngoc-trai`, `villa-sao-bien`, `villa-san-ho`).
    - Thêm danh sách các đặc quyền / điểm nhấn (Highlights).
    - Thống kê số lượng căn biệt thự đang thuộc phân khu đó.
  - Tích hợp nút xem nhanh trang phân khu ngoài client (`/villas/zone/:slug`).

- [ ] **Step 2: Đăng ký route `/admin/zones` trong `app.routes.ts`**

- [ ] **Step 3: Kiểm tra build Frontend**
Run: `npm run build`
Expected: `Application bundle generation complete`

---

### Task 7: Frontend Client - Hero Banner Slider & Trang Phân Khu Villa

**Files:**
- Modify: `frontend/src/app/features/home/components/hero-section/hero-section.component.ts`
- Modify: `frontend/src/app/features/home/components/hero-section/hero-section.component.html`
- Create: `frontend/src/app/features/villas/zone-detail/zone-detail.component.ts`
- Create: `frontend/src/app/features/villas/zone-detail/zone-detail.component.html`
- Create: `frontend/src/app/features/villas/zone-detail/zone-detail.component.scss`
- Modify: `frontend/src/app/layout/main-layout/header/header.component.html`
- Modify: `frontend/src/app/app.routes.ts`

- [ ] **Step 1: Nâng cấp `HeroSectionComponent` thành Dynamic Slider Carousel**
  - Tự động gọi `BannerService.getActiveBanners()`.
  - Có các tính năng:
    - Tự động chuyển slide sau mỗi 6 giây (hủy interval khi destroy component).
    - Nút Next / Prev hình tròn mờ kính sang trọng ở hai bên.
    - Thanh Dots Indicator ở dưới đáy banner.
    - Hiệu ứng chuyển động mượt mà (Fade transition).
    - Fallback an toàn hiển thị banner mặc định nếu chưa có dữ liệu.

- [ ] **Step 2: Tạo `ZoneDetailComponent` (`/villas/zone/:slug`)**
  - Lắng nghe param `:slug` từ `ActivatedRoute`.
  - Gọi API `zoneService.getZoneBySlug(slug)`:
    - Hiển thị Banner phân khu lớn với tiêu đề và các thẻ đặc quyền vị trí.
    - Thanh lọc nhanh (Lọc theo số phòng ngủ, mức giá, tiện ích có bể bơi).
    - Lưới danh sách các căn biệt thự thuộc phân khu.
    - Nút Đặt Ngay / Xem Chi Tiết.

- [ ] **Step 3: Cập nhật liên kết Header Menu**
  - `VILLA NGỌC TRAI` ➔ `[routerLink]="['/villas/zone/villa-ngoc-trai']"`
  - `VILLA SAO BIỂN` ➔ `[routerLink]="['/villas/zone/villa-sao-bien']"`
  - `VILLA SAN HÔ` ➔ `[routerLink]="['/villas/zone/villa-san-ho']"`
  - Đóng menu mobile khi người dùng bấm chọn liên kết.

- [ ] **Step 4: Kiểm tra build Frontend**
Run: `npm run build`
Expected: `Application bundle generation complete`

---

### Task 8: Kiểm Thử Tích Hợp & Xác Nhận Toàn Bộ Hệ Thống

**Files:** Toàn bộ hệ thống Backend & Frontend

- [ ] **Step 1: Biên dịch Backend & chạy kiểm thử**
Run: `./mvnw test-compile -DskipTests`
Expected: `BUILD SUCCESS`

- [ ] **Step 2: Biên dịch Frontend Production Bundle**
Run: `npm run build`
Expected: `Application bundle generation complete`

- [ ] **Step 3: Kiểm tra luồng hoạt động thực tế**
  - Đăng nhập Admin ➔ Truy cập `/admin/banners` ➔ Thêm/Sửa/Bật tắt banner.
  - Truy cập `/admin/zones` ➔ Đổi banner phân khu Ngọc Trai, Sao Biển, San Hô.
  - Ra Trang chủ ➔ Kiểm tra Slider tự động lướt qua các banner vừa tạo.
  - Bấm vào menu **VILLA NGỌC TRAI** ➔ Kiểm tra trang phân khu hiển thị đúng các căn biệt thự thuộc phân khu Ngọc Trai.
