# Kế Hoạch Triển Khai: Dọn Sạch Dữ Liệu Mẫu & Chuẩn Hóa Empty State Cho Trang Quản Lý Biệt Thự

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Dọn sạch toàn bộ dữ liệu mẫu hardcode ở cả Frontend Angular và Backend Database trên trang Quản lý Biệt Thự (`http://localhost:4200/admin/villas`), chuyển toàn bộ chỉ số KPI sang tính toán động 100%, và thiết kế các giao diện Empty State 5 sao để người quản trị tự tay tạo dữ liệu từ con số 0.

**Architecture:** 
1. Tắt auto-seed các thực thể villa, room, booking trong `DataInitializer.java` (giữ nguyên user accounts) và thực thi script SQL dọn sạch các bảng liên quan.
2. Tại `room-management.component.ts`, xóa bỏ các mảng mock dữ liệu tĩnh (`customZones`, `roomTypes`, `cleaningCards`), chuyển đổi 5 chỉ số KPI sang getter tính toán động 100%.
3. Tại `room-management.component.html`, hiển thị số liệu thực tế trên 5 thẻ KPI và thêm Empty State sang trọng cho Phân khu, Loại giường, Lưới biệt thự, đồng thời chuẩn hóa dropdown form thêm villa.

**Tech Stack:** Angular 18, TypeScript, Tailwind CSS, Spring Boot 3, MySQL.

## Global Constraints
- Không làm mất tài khoản quản trị `admin@auraholdings.vn` / `admin123` và các tài khoản nhân viên.
- Không gây lỗi ràng buộc khóa ngoại (Foreign Key Constraints) khi dọn dẹp dữ liệu MySQL.
- Mã nguồn Angular phải vượt qua kiểm tra biên dịch `npm run build` với 0 lỗi cú pháp và type error.
- Giữ nguyên toàn bộ giao diện và chức năng của các modal cấu hình (Modal 7, 8, 9, 10...).

---

### Task 1: Vô Hiệu Hóa Auto-Seeding & Dọn Dẹp Cơ Sở Dữ Liệu MySQL

**Files:**
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/DataInitializer.java:50-71`
- Database: MySQL database `booking_hotel`

**Interfaces:**
- Consumes: MySQL tables (`booking_details`, `payments`, `service_dispatches`, `housekeeping_tasks`, `service_recovery_tickets`, `reviews`, `bookings`, `rooms`, `villas`, `villa_types`, `room_types`)
- Produces: Cơ sở dữ liệu sạch không chứa villa hoặc booking mẫu; giữ nguyên bảng `users`.

- [ ] **Step 1: Cập nhật phương thức `run()` trong `DataInitializer.java`**

Mở file [`DataInitializer.java`](file:///d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/DataInitializer.java) và chú thích lại (comment out) các lời gọi hàm khởi tạo dữ liệu mẫu cho villa, booking, review:

```java
    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting luxury homestay & villa resort database verification and seeding...");

        try {
            jdbcTemplate.execute("ALTER TABLE rooms MODIFY COLUMN room_type_id BIGINT NULL");
        } catch (Exception e) {
            log.warn("Could not alter rooms.room_type_id to nullable: {}", e.getMessage());
        }

        initUsers();
        // Vô hiệu hóa auto-seeding dữ liệu mẫu villa & booking để người dùng thêm thủ công
        // initVillaTypesAndVillas();
        initExtraServices();
        initShiftsAndSchedules();
        initYieldRules();
        initPromotions();
        // initBookingsAndDispatches();
        initPaymentsAndClosings();
        // initReviewsAndRecovery();

        log.info("Homestay & Villa database initialization completed successfully (Clean state)!");
    }
```

- [ ] **Step 2: Chạy lệnh dọn sạch dữ liệu các bảng phòng và đơn đặt phòng trong MySQL**

Thực thi câu lệnh SQL xóa dữ liệu trong database MySQL thông qua script hoặc `mysql` CLI / PowerShell:

```sql
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE booking_details;
TRUNCATE TABLE payments;
TRUNCATE TABLE service_dispatches;
TRUNCATE TABLE housekeeping_tasks;
TRUNCATE TABLE service_recovery_tickets;
TRUNCATE TABLE reviews;
TRUNCATE TABLE bookings;
TRUNCATE TABLE rooms;
TRUNCATE TABLE villas;
TRUNCATE TABLE villa_types;
TRUNCATE TABLE room_types;
SET FOREIGN_KEY_CHECKS = 1;
```

- [ ] **Step 3: Xác minh cơ sở dữ liệu đã sạch**

Chạy câu lệnh kiểm tra số lượng bản ghi:
Expected:
- `villas`: 0
- `rooms`: 0
- `villa_types`: 0
- `users`: >= 5 (các tài khoản admin và nhân viên được bảo toàn)

- [ ] **Step 4: Commit thay đổi Backend**

```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/DataInitializer.java
git commit -m "chore(backend): disable mock villas and bookings seeding in DataInitializer"
```

---

### Task 2: Làm Sạch Dữ Liệu Mock & Thêm Logic KPI Động Trong TypeScript Component

**Files:**
- Modify: `frontend/src/app/admin/room-management/room-management.component.ts`

**Interfaces:**
- Consumes: `AdminHousekeepingService.getRooms()`, `AdminHousekeepingService.getRoomTypes()`
- Produces:
  - `customZones: CustomZoneItem[] = []`
  - `roomTypes: RoomTypeDisplayItem[] = []`
  - `rooms: RoomCard[] = []`
  - `cleaningCards: any[] = []`
  - Getters: `totalVillasCount`, `occupiedVillasCount`, `availableVillasCount`, `cleaningVillasCount`, `maintenanceVillasCount`, `occupancyPercentage`, `availablePercentage`

- [ ] **Step 1: Xóa các mảng mock tĩnh ban đầu**

Trong file [`room-management.component.ts`](file:///d:/booking_hotel/frontend/src/app/admin/room-management/room-management.component.ts):
- Đặt `customZones: CustomZoneItem[] = [];`
- Đặt `roomTypes: RoomTypeDisplayItem[] = [];`
- Đặt `cleaningCards: any[] = [];`
- Trong `initCustomZones()`, nếu `localStorage` có chứa danh sách cũ thì xóa khóa hoặc cho phép người dùng khởi tạo từ mảng rỗng nếu chưa từng lưu.
- Đặt giá trị mặc định cho `roomForm`:
  ```ts
  roomForm = {
    roomNumber: '',
    floor: 1,
    roomTypeId: null as number | null,
    zone: '',
    status: 'AVAILABLE',
    ozoneStatus: 'STERILIZED',
    basePrice: 25000000,
    imageUrl: '',
    imageName: '',
    isUploadingImage: false,
  };
  ```

- [ ] **Step 2: Thêm các getter tính toán động 100% cho 5 thẻ KPI**

Thêm các getter sau vào `RoomManagementComponent`:

```typescript
  get totalVillasCount(): number {
    return this.rooms?.length || 0;
  }

  get occupiedVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'OCCUPIED').length || 0;
  }

  get availableVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'AVAILABLE').length || 0;
  }

  get cleaningVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'CLEANING').length || 0;
  }

  get maintenanceVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'MAINTENANCE').length || 0;
  }

  get occupancyPercentage(): number {
    if (this.totalVillasCount === 0) return 0;
    return Math.round((this.occupiedVillasCount / this.totalVillasCount) * 100);
  }

  get availablePercentage(): number {
    if (this.totalVillasCount === 0) return 0;
    return Math.round((this.availableVillasCount / this.totalVillasCount) * 100);
  }
```

- [ ] **Step 3: Cập nhật hàm `loadRoomTypes()` và `loadRooms()` khi API trả về rỗng**

Đảm bảo khi API trả về mảng rỗng `[]`:
- `this.roomTypes = []` (không giữ lại 4 card mock ban đầu).
- `this.rooms = []` và `this.filteredRooms = []`.
- `this.applyFilters()` và `buildGroupedZones()` xử lý an toàn khi mảng rỗng.

- [ ] **Step 4: Kiểm tra không còn lỗi compile TypeScript**

Run: `npx tsc --noEmit -p frontend/tsconfig.app.json`
Expected: 0 errors.

- [ ] **Step 5: Commit thay đổi Component TypeScript**

```bash
git add frontend/src/app/admin/room-management/room-management.component.ts
git commit -m "feat(admin): remove mock data arrays and add dynamic KPI calculation getters"
```

---

### Task 3: Cập Nhật Template HTML Với KPI Động & Thiết Kế Giao Diện Empty State

**Files:**
- Modify: `frontend/src/app/admin/room-management/room-management.component.html`

**Interfaces:**
- Consumes: Getters từ Task 2, `customZones`, `roomTypes`, `rooms`, `filteredRooms`
- Produces: Giao diện sạch sẽ, hiển thị số liệu động 100% và Empty State chuẩn resort 5 sao khi danh mục rỗng.

- [ ] **Step 1: Cập nhật 5 thẻ KPI đầu trang (Section 1)**

Thay thế các giá trị số và thanh phần trăm hardcode bằng getter động:
- Card 1 (TỔNG BIỆT THỰ): `{{ totalVillasCount }}` căn, `{{ customZones.length }} Phân khu`.
- Card 2 (ĐANG CÓ KHÁCH): `{{ occupiedVillasCount }}` / `{{ totalVillasCount }} Căn`, thanh width: `[style.width.%]="occupancyPercentage"`, `{{ occupancyPercentage }}% Công suất`.
- Card 3 (SẴN SÀNG ĐÓN KHÁCH): `{{ availableVillasCount }}` Căn sạch, `{{ availablePercentage }}% Trống`.
- Card 4 (ĐANG DỌN): `{{ cleaningVillasCount }}` Đang xử lý.
- Card 5 (BẢO TRÌ): `{{ maintenanceVillasCount }}` Căn ngưng dùng.

- [ ] **Step 2: Cập nhật Section 2 (Thanh Phân Khu)**

Khi `customZones.length === 0`:
Hiển thị một thông báo gợi ý thanh lịch:
```html
<div *ngIf="customZones.length === 0" class="p-3 bg-slate-50 border border-dashed border-slate-200 rounded-xl text-center text-xs text-slate-500">
  Chưa có phân khu nào được tạo. Bấm <strong class="text-[#0284c7] cursor-pointer" (click)="openCreateZoneModal()">+ Thêm Phân Khu</strong> để bắt đầu phân cụm biệt thự.
</div>
```

- [ ] **Step 3: Cập nhật Section 3 (Tổng Quan Các Loại Giường)**

Khi `roomTypes.length === 0`:
Hiển thị Empty State sang trọng:
```html
<div *ngIf="roomTypes.length === 0" class="bg-white border-2 border-dashed border-slate-200 rounded-2xl p-8 text-center space-y-3">
  <div class="w-14 h-14 mx-auto rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center">
    <span class="material-symbols-outlined text-[32px]">hotel</span>
  </div>
  <div>
    <h3 class="text-sm font-bold text-slate-800">Chưa Có Danh Mục Loại Giường</h3>
    <p class="text-xs text-slate-400 mt-1 max-w-md mx-auto">
      Hệ thống chưa có tiêu chuẩn loại giường và sức chứa. Vui lòng khởi tạo loại giường đầu tiên để sẵn sàng gán cho các căn biệt thự.
    </p>
  </div>
  <button
    type="button"
    (click)="openCreateRoomTypeModal()"
    class="px-4 py-2 bg-[#0284c7] hover:bg-[#0369a1] text-white rounded-xl shadow-xs transition font-bold text-xs inline-flex items-center space-x-1.5 cursor-pointer"
  >
    <span class="material-symbols-outlined text-[16px]">add_circle</span>
    <span>+ Thêm Loại Giường Đầu Tiên</span>
  </button>
</div>
```

- [ ] **Step 4: Cập nhật Section 4 (Lưới & Danh Sách Biệt Thự)**

Khi `rooms.length === 0`:
Hiển thị giao diện Empty State quy trình 3 bước trực quan:
```html
<div *ngIf="rooms.length === 0" class="bg-white border border-slate-200/80 rounded-2xl p-8 sm:p-12 shadow-2xs text-center space-y-6">
  <div class="w-16 h-16 mx-auto rounded-2xl bg-sky-50 text-[#0284c7] flex items-center justify-center shadow-xs">
    <span class="material-symbols-outlined text-[36px]">holiday_village</span>
  </div>
  <div class="max-w-md mx-auto">
    <h3 class="text-base font-bold text-slate-900">Chưa Có Biệt Thự Nào Trong Hệ Thống</h3>
    <p class="text-xs text-slate-500 mt-1.5 leading-relaxed">
      Dữ liệu mẫu đã được dọn sạch hoàn toàn. Bạn có thể bắt đầu thiết lập resort nghỉ dưỡng theo quy trình 3 bước chuẩn bên dưới:
    </p>
  </div>

  <!-- Sơ đồ 3 bước thiết lập -->
  <div class="grid grid-cols-1 sm:grid-cols-3 gap-3.5 max-w-2xl mx-auto text-left">
    <div class="p-3.5 bg-slate-50 border border-slate-200/70 rounded-xl space-y-1.5">
      <div class="flex items-center space-x-2 text-amber-600 font-bold text-xs">
        <span class="w-5 h-5 rounded-full bg-amber-100 flex items-center justify-center text-[11px]">1</span>
        <span>Loại Giường</span>
      </div>
      <p class="text-[11px] text-slate-500">Tạo quy cách giường ngủ và số khách tối đa (sức chứa).</p>
    </div>
    <div class="p-3.5 bg-slate-50 border border-slate-200/70 rounded-xl space-y-1.5">
      <div class="flex items-center space-x-2 text-sky-600 font-bold text-xs">
        <span class="w-5 h-5 rounded-full bg-sky-100 flex items-center justify-center text-[11px]">2</span>
        <span>Phân Khu</span>
      </div>
      <p class="text-[11px] text-slate-500">Tạo các phân khu nghỉ dưỡng (Biển, Vách Đá, Hồ Nước...).</p>
    </div>
    <div class="p-3.5 bg-slate-50 border border-slate-200/70 rounded-xl space-y-1.5">
      <div class="flex items-center space-x-2 text-emerald-600 font-bold text-xs">
        <span class="w-5 h-5 rounded-full bg-emerald-100 flex items-center justify-center text-[11px]">3</span>
        <span>Khởi Tạo Biệt Thự</span>
      </div>
      <p class="text-[11px] text-slate-500">Nhập số hiệu biệt thự, chọn loại giường và gán đơn giá thuê.</p>
    </div>
  </div>

  <div>
    <button
      type="button"
      (click)="openCreateRoomModal()"
      class="px-5 py-2.5 bg-[#0284c7] hover:bg-[#0369a1] text-white rounded-xl shadow-xs transition font-bold text-xs inline-flex items-center space-x-2 cursor-pointer"
    >
      <span class="material-symbols-outlined text-[18px]">add_home</span>
      <span>Khởi Tạo Biệt Thự Đầu Tiên</span>
    </button>
  </div>
</div>
```

- [ ] **Step 5: Làm sạch dropdowns trong Modal 7 (Thêm/Sửa Biệt Thự)**

- Xóa toàn bộ các thẻ `<option *ngIf="roomTypesList.length === 0" [value]="17">...` tĩnh.
- Thay bằng:
```html
<option *ngIf="roomTypesList.length === 0" [value]="null" disabled selected>
  Chưa có loại giường nào — Vui lòng bấm + Thêm Loại Giường trước
</option>
```
- Tương tự cho `customZones`:
```html
<option *ngIf="customZones.length === 0" [value]="''" disabled selected>
  Chưa có phân khu nào — Vui lòng bấm + Thêm Phân Khu trước
</option>
```

- [ ] **Step 6: Kiểm tra định dạng và biên dịch HTML**

Run: `npx prettier --write frontend/src/app/admin/room-management/room-management.component.html`
Run: `npm run build` trong `frontend/`
Expected: Build thành công 100%.

- [ ] **Step 7: Commit thay đổi HTML**

```bash
git add frontend/src/app/admin/room-management/room-management.component.html
git commit -m "feat(admin): add dynamic KPI bindings and luxury empty states for villas and room types"
```

---

### Task 4: Kiểm Thử Toàn Trình (End-to-End Verification)

**Files:** N/A (Kiểm thử thực tế hệ thống)

- [ ] **Step 1: Khởi động lại hoặc kiểm tra Backend**
- [ ] **Step 2: Mở trình duyệt tại `http://localhost:4200/admin/villas`**
- [ ] **Step 3: Kiểm tra trạng thái rỗng hoàn toàn**
  - KPI: 0 biệt thự, 0% công suất, 0 căn sạch.
  - Phân khu: Hiển thị hướng dẫn thêm phân khu.
  - Loại giường: Hiển thị Empty State với nút "+ Thêm Loại Giường Đầu Tiên".
  - Biệt thự: Hiển thị Empty State quy trình 3 bước.
- [ ] **Step 4: Thực hiện thêm dữ liệu mẫu thủ công bằng tay từ UI**
  - Bấm thêm 1 loại giường mới (ví dụ: "Oceanfront King Suite", sức chứa 4 khách).
  - Bấm thêm 1 phân khu mới (ví dụ: "Khu Biển").
  - Bấm "Khởi Tạo Biệt Thự Mới", nhập số hiệu "Villa #101", giá "15.000.000 VNĐ", chọn loại giường và phân khu vừa tạo.
  - Lưu và xác nhận:
    - Thẻ villa xuất hiện chính xác trên lưới.
    - KPI nhảy lên 1 căn, 1 căn sẵn sàng.
    - Thông tin đơn giá và loại giường khớp 100%.
