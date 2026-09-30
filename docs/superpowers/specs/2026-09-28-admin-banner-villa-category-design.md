# Thiết Kế Chi Tiết: Quản Lý Banner Trang Chủ & Trang Phân Loại Villa (CMS)

## 1. Mục Tiêu & Bối Cảnh Nghiệp Vụ

### 1.1. Bối cảnh
- Hiện tại, Banner trang chủ (`hero-section`) đang sử dụng hình ảnh và nội dung tĩnh được gắn cố định trong mã nguồn. Khi cần thay đổi hình ảnh quảng bá cho dịp lễ, mùa du lịch hoặc chiến dịch tiếp thị, ban quản trị không thể chủ động tùy biến.
- Thanh Menu chính của website đã được nâng cấp thành mẫu chuyên nghiệp gồm các mục phân khu: **VILLA NGỌC TRAI**, **VILLA SAO BIỂN**, **VILLA SAN HÔ**, **TÌM VILLA**. Khách hàng cần có trang giao diện riêng cho từng phân khu để xem đặc điểm, vị trí, tiện ích và danh sách các căn biệt thự thuộc phân khu tương ứng.

### 1.2. Mục tiêu đạt được
1. **Quản lý Banner Trang chủ (Admin)**: Ban quản trị có thể tạo, chỉnh sửa, xóa, bật/tắt và sắp xếp thứ tự hiển thị của các slide banner.
2. **Hero Banner Slider Động (Client)**: Trang chủ tự động tải các banner đang hoạt động và trình diễn dưới dạng Carousel / Slider mượt mà, sang trọng.
3. **Quản lý Phân Khu Villa (Admin)**: Quản trị viên có thể tùy biến thông tin phân khu (ảnh bìa, tiêu đề, mô tả, thẻ tiện ích nổi bật).
4. **Trang Phân Khu Villa Độc Lập (Client)**: Cung cấp route `/villas/zone/:slug` hiển thị đầy đủ hình ảnh, thông điệp giới thiệu và danh sách các căn villa đã được tự động lọc theo đúng phân khu.

---

## 2. Thiết Kế Cơ Sở Dữ Liệu & Entity

### 2.1. Thực thể mới: `HomeBanner.java` (Bảng `home_banners`)
* **Package**: `com.phungvanlong.booking_hotel.entity`
* **Kế thừa**: `BaseEntity` (có sẵn `createdAt`, `updatedAt`)
* **Các thuộc tính**:
  * `id`: `Long`, Khóa chính tự tăng (`@GeneratedValue(strategy = GenerationType.IDENTITY)`).
  * `title`: `String`, `@Column(nullable = false, length = 255)` - Tiêu đề chính của banner.
  * `subtitle`: `String`, `@Column(length = 255)` - Tiêu đề phụ / huy hiệu phía trên.
  * `description`: `String`, `@Column(columnDefinition = "TEXT")` - Đoạn mô tả ngắn.
  * `imageUrl`: `String`, `@Column(nullable = false, length = 500)` - Đường dẫn ảnh banner chất lượng cao.
  * `mobileImageUrl`: `String`, `@Column(length = 500)` - Đường dẫn ảnh tối ưu cho di động (tùy chọn).
  * `ctaText`: `String`, `@Column(length = 100)` - Nhãn nút hành động (ví dụ: "Đặt Phòng Ngay", "Xem Biệt Thự").
  * `ctaLink`: `String`, `@Column(length = 255)` - Đường dẫn khi bấm nút (ví dụ: `"#booking-widget"`, `"/villas/zone/villa-ngoc-trai"`).
  * `displayOrder`: `Integer`, `@Column(name = "display_order", nullable = false)` - Thứ tự hiển thị slide (1, 2, 3...).
  * `isActive`: `Boolean`, `@Column(name = "is_active", nullable = false)` - Trạng thái hiển thị (mặc định `true`).
  * `badgesJson`: `String`, `@Column(name = "badges_json", columnDefinition = "TEXT")` - Danh sách huy hiệu cam kết lưu dạng JSON.

### 2.2. Nâng cấp Thực thể: `Zone.java` (Bảng `zones`)
Bổ sung các cột phục vụ trang giới thiệu phân khu:
* `slug`: `String`, `@Column(unique = true, length = 120)` - Slug URL (ví dụ: `villa-ngoc-trai`, `villa-sao-bien`, `villa-san-ho`).
* `bannerUrl`: `String`, `@Column(name = "banner_url", length = 500)` - Ảnh bìa lớn đại diện cho phân khu.
* `highlights`: `String`, `@Column(columnDefinition = "TEXT")` - Các điểm nhấn đặc quyền phân cách bằng dấu chấm phẩy hoặc JSON (ví dụ: *"Sát biển 50m; Bể bơi riêng 4 mùa; Dàn Karaoke cao cấp"*).
* `displayOrder`: `Integer`, `@Column(name = "display_order")` - Thứ tự hiển thị.

---

## 3. Thiết Kế RESTful APIs

### 3.1. Public APIs (Dành cho Client Frontend)
* `GET /api/v1/public/banners`:
  * Trả về danh sách banner đang `isActive = true`, sắp xếp theo `displayOrder ASC`.
  * Response: `ApiResponse<List<HomeBannerResponse>>`
* `GET /api/v1/public/zones`:
  * Trả về danh sách tất cả phân khu đang hoạt động.
  * Response: `ApiResponse<List<ZoneResponse>>`
* `GET /api/v1/public/zones/{slug}`:
  * Trả về chi tiết phân khu theo slug kèm danh sách các căn biệt thự thuộc phân khu đó.
  * Response: `ApiResponse<ZoneDetailResponse>`

### 3.2. Admin APIs (Bảo mật quyền ROLE_ADMIN)
* **Quản lý Banner**:
  * `GET /api/v1/admin/banners`: Lấy tất cả banner (kể cả ẩn).
  * `POST /api/v1/admin/banners`: Tạo mới banner (`HomeBannerRequest`).
  * `PUT /api/v1/admin/banners/{id}`: Cập nhật banner (`HomeBannerRequest`).
  * `PATCH /api/v1/admin/banners/{id}/status`: Bật / Tắt trạng thái hiển thị.
  * `DELETE /api/v1/admin/banners/{id}`: Xóa banner.
* **Quản lý Phân Khu**:
  * `GET /api/v1/admin/zones`: Danh sách phân khu kèm số lượng villa trực thuộc.
  * `POST /api/v1/admin/zones`: Thêm phân khu mới (tự động sinh slug nếu để trống).
  * `PUT /api/v1/admin/zones/{id}`: Cập nhật thông tin phân khu, banner và highlights.
  * `DELETE /api/v1/admin/zones/{id}`: Xóa phân khu (kiểm tra ràng buộc không có villa).

---

## 4. Thiết Kế Giao Diện Frontend (Angular)

### 4.1. Khối Menu Quản Trị: `NỘI DUNG & WEBSITE` (Sidebar Admin)
Cập nhật `admin-layout.component.html`:
```html
<!-- KHỐI 5: NỘI DUNG & WEBSITE -->
<div class="space-y-1 pt-1">
  <div class="px-3 pt-1 pb-1 text-[11px] font-black uppercase tracking-wider text-slate-400 flex items-center space-x-1.5">
    <span class="material-symbols-outlined text-[16px] text-sky-500">web</span>
    <span class="text-slate-600 font-bold">NỘI DUNG & WEBSITE</span>
  </div>

  <!-- 12. Banner Trang Chủ -->
  <a routerLink="/admin/banners" routerLinkActive="bg-[#0284c7] text-white font-bold shadow-sm" class="...">
    <span class="material-symbols-outlined text-[18px] mr-2.5">view_carousel</span>
    <span>12. Banner Trang Chủ</span>
  </a>

  <!-- 13. Phân Khu & Phân Loại Villa -->
  <a routerLink="/admin/zones" routerLinkActive="bg-[#0284c7] text-white font-bold shadow-sm" class="...">
    <span class="material-symbols-outlined text-[18px] mr-2.5">holiday_village</span>
    <span>13. Phân Khu & Danh Mục</span>
  </a>
</div>
```

### 4.2. Màn hình Quản Lý Banner (`/admin/banners`)
Component: `AdminBannerManagementComponent`
* **Giao diện danh sách**:
  * Hiển thị lưới thẻ (Cards Grid) hoặc bảng có hình ảnh thu nhỏ của banner.
  * Thẻ hiển thị: Ảnh preview, Tiêu đề, Trạng thái (Đang hiển thị / Đã ẩn), Thứ tự hiển thị, Nút CTA.
  * Các nút thao tác nhanh: Đổi thứ tự hiển thị, Bật/Tắt hiển thị, Sửa, Xóa.
* **Modal Thêm / Sửa Banner**:
  * Biểu mẫu: Tiêu đề chính, Tiêu đề phụ, Mô tả, URL hình ảnh (hỗ trợ chọn nhanh ảnh đẹp có sẵn), Nút CTA (Tên nút, Link), Thứ tự.
  * Khung xem trước trực tiếp (Live Preview) bố cục banner để quản trị viên đánh giá trước khi lưu.

### 4.3. Màn hình Quản Lý Phân Khu & Phân Loại (`/admin/zones`)
Component: `AdminZoneManagementComponent`
* Hiển thị danh sách các phân khu: Ngọc Trai, Sao Biển, San Hô...
* Chỉnh sửa ảnh Banner đại diện của phân khu, mô tả giới thiệu, danh sách điểm nhấn đặc quyền.
* Thống kê số lượng căn biệt thự đang thuộc phân khu đó.

### 4.4. Hero Banner Slider trên Trang Chủ (`hero-section.component.ts`)
* Tự động gọi API `GET /api/v1/public/banners` để lấy danh sách banner active.
* Nếu API trả về danh sách banner:
  * Trình chiếu Slider với hiệu ứng chuyển cảnh mượt mà.
  * Tự động chạy sau mỗi 6 giây, tạm dừng khi người dùng rê chuột lên banner.
  * Tích hợp 2 nút điều hướng Trái / Phải (Prev / Next) và thanh chấm tròn (Pagination Dots).
* Nếu không có banner hoặc đang tải: Sử dụng banner mặc định làm fallback an toàn.

### 4.5. Trang Phân Loại Villa Riêng Biệt (`/villas/zone/:slug`)
Component: `ZoneDetailComponent`
* **Route**: `{ path: 'villas/zone/:slug', component: ZoneDetailComponent }`
* **Cấu trúc trang**:
  1. **Top Zone Hero**: Hiển thị ảnh bìa phân khu lớn (`bannerUrl`), Tiêu đề phân khu (*"KHU BIỆT THỰ NGHỈ DƯỠNG NGỌC TRAI"*), Mô tả và các thẻ đặc quyền vị trí.
  2. **Thanh Bộ Lọc Nhanh**:
     * Lọc theo số phòng ngủ: 3 - 5 Phòng, 6 - 8 Phòng, 9 - 15 Phòng.
     * Lọc theo mức giá.
     * Lọc tiện ích: Có hồ bơi riêng, karaoke, gần biển.
  3. **Danh Sách Biệt Thự Trực Thuộc**:
     * Lưới các căn Villa thuộc phân khu đó.
     * Thẻ Villa hiển thị: Ảnh đại diện, Mã căn, Tên căn, Số phòng ngủ, Số khách tối đa, Tiện ích chính, Giá thuê/đêm.
     * Nút "Xem Chi Tiết" và "Đặt Ngay".

### 4.6. Liên Kết Menu Trên Header
Cập nhật thanh Navigation màu xanh trên [`header.component.html`](file:///d:/booking_hotel/frontend/src/app/layout/main-layout/header/header.component.html):
* `VILLA NGỌC TRAI` ➔ `routerLink="/villas/zone/villa-ngoc-trai"`
* `VILLA SAO BIỂN` ➔ `routerLink="/villas/zone/villa-sao-bien"`
* `VILLA SAN HÔ` ➔ `routerLink="/villas/zone/villa-san-ho"`
* `TÌM VILLA ▾` ➔ Các đường dẫn lọc tương ứng.

---

## 5. Kế Hoạch Triển Khai Từng Bước (Implementation Phases)

### Giai đoạn 1: Backend Data Model & APIs
1. Tạo thực thể `HomeBanner.java` và cập nhật thực thể `Zone.java` (thêm `slug`, `bannerUrl`, `highlights`, `displayOrder`).
2. Tạo Repository `HomeBannerRepository` và cập nhật `ZoneRepository`.
3. Xây dựng DTOs: `HomeBannerRequest`, `HomeBannerResponse`, `ZoneDetailResponse`.
4. Viết Service & Controller cho Banner (`HomeBannerService`, `AdminBannerController`, `PublicBannerController`).
5. Cập nhật `AdminZoneService` và `PublicZoneController` để cung cấp endpoint lấy phân khu theo slug kèm danh sách villa.

### Giai đoạn 2: Admin CMS Frontend
1. Cập nhật Sidebar Admin (`admin-layout.component.html`) thêm nhóm `NỘI DUNG & WEBSITE`.
2. Tạo Service `AdminBannerService` và `ZoneService` ở Frontend.
3. Tạo Component `AdminBannerManagementComponent` (`/admin/banners`) với đầy đủ tính năng CRUD và xem trước.
4. Cập nhật Component `AdminZoneManagementComponent` (`/admin/zones`) để quản lý banner và mô tả phân khu.

### Giai đoạn 3: Client Hero Slider & Zone Page
1. Nâng cấp `HeroSectionComponent` thành Slider Banner Carousel tự động chuyển ảnh với dữ liệu động.
2. Tạo Component `ZoneDetailComponent` (`/villas/zone/:slug`) hiển thị thông tin phân khu và danh sách villa lọc theo phân khu.
3. Liên kết router trên Header Menu và kiểm thử toàn bộ luồng hoạt động.
