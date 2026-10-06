# Staff Shift Scheduling, Persistence & Admin Approval Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Chuyển đổi bảng phân ca nhân sự tuần từ dữ liệu mock/modulo sang dữ liệu thật 100% trong database (`staff_schedules`, `shifts`), đồng thời xây dựng luồng đăng ký ca tuần mới từ nhân viên và phê duyệt ca trực từ phía Admin.

**Architecture:** 
1. Backend: Tận dụng và hoàn thiện `Shift` và `StaffSchedule` entity, khởi tạo seed data ca trực (`Ca Sáng`, `Ca Chiều`, `Ca Đêm`, `On-Call VIP`, `OFF Nghỉ`). Xây dựng entity `WeeklyShiftRegistration` và `WeeklyShiftRegistrationDetail` để lưu đơn đăng ký ca 7 ngày của nhân viên.
2. Dịch vụ phân ca: Cập nhật `AdminStaffServiceImpl` đọc và ghi dữ liệu thực tế từ `StaffScheduleRepository`. Cung cấp API cập nhật từng ô ca trực, API tạo ca tự động (AI Auto-Schedule) lưu trực tiếp xuống DB.
3. Luồng phê duyệt: Cung cấp API cho nhân viên gửi đăng ký ca 7 ngày tuần tới. Admin xem danh sách đơn đăng ký ca tuần chờ duyệt và bấm "Phê Duyệt Lịch Tuần" -> tự động tạo 7 bản ghi `StaffSchedule` tương ứng trong DB và đồng bộ ngay lên Bảng phân ca Live Shift Roster.
4. Frontend: Cập nhật `StaffManagementComponent` và `HousekeepingPortalComponent` gọi API thật, cập nhật giao diện Admin với bảng duyệt đơn đăng ký ca tuần mới.

**Tech Stack:** Spring Boot 3.3.4, Java 17, Spring Data JPA, Hibernate, MySQL, Angular 17/18 Standalone Components, Tailwind CSS, TypeScript.

## Global Constraints
- Context path backend là `/api/v1` (toàn bộ endpoint admin nằm dưới `/api/v1/admin/staff/...`).
- Sử dụng mô hình chuẩn ApiResponse: `{ success: boolean, message: string, data: T }`.
- Giao diện Admin và Staff giữ nguyên phong cách Luxury Resort UI hiện hữu (Tailwind CSS, Material Symbols).
- Không làm gián đoạn các tính năng chấm công Biometric/FaceID và Đổi ca (Shift Swap) hiện có.

---

### Task 1: Seed Default Shifts & Enhance StaffSchedule Entity

**Files:**
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/StaffSchedule.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/DataInitializer.java`
- Test: Build and check database records for shifts.

**Interfaces:**
- `Shift`: Có đủ các loại ca cơ bản: Ca Sáng (`MORNING`), Ca Chiều (`AFTERNOON`), Ca Đêm (`NIGHT`), On-Call VIP (`ONCALL`), OFF Nghỉ (`OFF`).
- `StaffSchedule`: Cho phép `shift` nullable nếu `isOff = true` hoặc liên kết trực tiếp tới ca `OFF Nghỉ`.

- [ ] **Step 1: Cập nhật `StaffSchedule.java` để cho phép `shift_id` có thể nullable khi `isOff = true`**
```java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = true)
    private Shift shift;
```

- [ ] **Step 2: Thêm hàm `initShifts()` trong `DataInitializer.java`**
Khởi tạo 5 ca làm việc chuẩn nếu bảng `shifts` chưa có dữ liệu:
1. `Ca Sáng` (06:00 - 14:30), colorCode: `#10b981` (emerald)
2. `Ca Chiều` (14:00 - 22:30), colorCode: `#0284c7` (sky)
3. `Ca Đêm` (22:00 - 06:30), colorCode: `#6366f1` (indigo)
4. `On-Call VIP` (00:00 - 23:59), colorCode: `#f59e0b` (amber)
5. `OFF Nghỉ` (00:00 - 00:00), colorCode: `#64748b` (slate)

- [ ] **Step 3: Khởi tạo dữ liệu lịch trực tuần mẫu (Seeding StaffSchedule) trong `DataInitializer.java`**
Tạo trước lịch trực thực tế cho các nhân viên có sẵn (`nam.reception`, `hoang.butler`, `hoa.housekeeping`, etc.) cho tuần hiện tại và tuần tới để bảng phân ca lập tức có dữ liệu thật.

---

### Task 2: Create WeeklyShiftRegistration Entities & Repository

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/WeeklyShiftRegistration.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/WeeklyShiftRegistrationDetail.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/WeeklyShiftRegistrationRepository.java`

**Interfaces:**
- `WeeklyShiftRegistration`:
  - `Long id`
  - `User staff`
  - `LocalDate weekStartDate`
  - `LocalDate weekEndDate`
  - `String status` (`PENDING`, `APPROVED`, `REJECTED`)
  - `String preferredZone`
  - `String notes`
  - `User approver`
  - `LocalDateTime approvedAt`
  - `String rejectionReason`
  - `List<WeeklyShiftRegistrationDetail> details`
- `WeeklyShiftRegistrationDetail`:
  - `Long id`
  - `WeeklyShiftRegistration registration`
  - `LocalDate workDate`
  - `String dayOfWeek` (T2, T3, T4, T5, T6, T7, CN)
  - `String shiftType` (MORNING, AFTERNOON, NIGHT, OFF)
  - `String note`

- [ ] **Step 1: Tạo `WeeklyShiftRegistration.java` và `WeeklyShiftRegistrationDetail.java`**
- [ ] **Step 2: Tạo `WeeklyShiftRegistrationRepository.java`** với các phương thức:
  - `List<WeeklyShiftRegistration> findByStatusOrderByCreatedAtDesc(String status)`
  - `Optional<WeeklyShiftRegistration> findByStaffIdAndWeekStartDate(Long staffId, LocalDate weekStartDate)`
  - `List<WeeklyShiftRegistration> findByStaffIdOrderByCreatedAtDesc(Long staffId)`

---

### Task 3: Backend Implementation for Real Schedule & Registration Approval

**Files:**
- Create DTOs:
  - `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/UpdateScheduleCellRequest.java`
  - `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/WeeklyShiftRegistrationRequest.java`
  - `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/ApproveWeeklyRegistrationRequest.java`
  - `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/WeeklyShiftRegistrationResponse.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/AdminStaffService.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/AdminStaffServiceImpl.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminStaffController.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/HousekeepingMobileController.java`

- [ ] **Step 1: Tạo các DTO Request / Response**
  - `UpdateScheduleCellRequest`: `staffId`, `workDate`, `shiftType`, `note`.
  - `WeeklyShiftRegistrationRequest`: `weekStartDate`, `days` (list of date, shiftType, dayOfWeek), `preferredZone`, `notes`.
  - `ApproveWeeklyRegistrationRequest`: `registrationId`, `approved` (boolean), `rejectionReason`.
  - `WeeklyShiftRegistrationResponse`: chuyển đổi thông tin đơn đăng ký và 7 ngày chi tiết gửi về client.

- [ ] **Step 2: Cập nhật `AdminStaffServiceImpl.java` để đọc dữ liệu từ `StaffScheduleRepository`**
  - Trong `getWeeklyRoster(LocalDate startDate)`:
    - Tìm kiếm `List<StaffSchedule> existingSchedules = staffScheduleRepository.findByWorkDateBetweenOrderByWorkDateAsc(startDate, endDate)`.
    - Với mỗi nhân viên và mỗi ngày từ `startDate` đến `endDate`:
      - Nếu có bản ghi trong `existingSchedules`, lấy đúng ca trực (`shift.getName()`), màu (`shift.getColorCode()`), trạng thái (`isOff ? "OFF" : "SCHEDULED"`).
      - Nếu chưa có bản ghi: Hiển thị trạng thái "Chưa phân ca" / "OFF Nghỉ" thay vì ép modulo giả lập.
  - Viết method `updateScheduleCell(UpdateScheduleCellRequest request)`:
    - Tìm `StaffSchedule` theo `staffId` và `workDate`. Nếu chưa có thì tạo mới, nếu có thì cập nhật.
    - Gán `Shift` tương ứng (`Ca Sáng`, `Ca Chiều`, `Ca Đêm`, `On-Call VIP`, `OFF Nghỉ`). Lưu vào DB.
  - Viết method `generateAiWeeklyRoster(LocalDate startDate)`:
    - Tự động sinh lịch trực cân bằng ca và lưu 7xN bản ghi `StaffSchedule` vào database cho cả tuần.
  - Viết methods cho đơn đăng ký ca:
    - `submitWeeklyRegistration(Long staffId, WeeklyShiftRegistrationRequest request)`
    - `getMyWeeklyRegistration(Long staffId, LocalDate weekStartDate)`
    - `getPendingWeeklyRegistrations()`
    - `processWeeklyRegistration(ApproveWeeklyRegistrationRequest request, String approverEmail)`:
      - Khi `approved == true`: duyệt đơn và tự động upsert 7 ngày vào bảng `staff_schedules`!

- [ ] **Step 3: Cung cấp API Endpoints trong `AdminStaffController.java`**
  - `POST /admin/staff/schedule/cell` -> `updateScheduleCell`
  - `POST /admin/staff/schedule/ai-generate` -> `generateAiWeeklyRoster`
  - `GET /admin/staff/weekly-registrations` -> `getPendingWeeklyRegistrations`
  - `POST /admin/staff/weekly-registrations/action` -> `processWeeklyRegistration`

- [ ] **Step 4: Cung cấp API Endpoints cho nhân viên (Staff Registration)**
  - `POST /housekeeping/shift-registration` để nhân viên submit đơn đăng ký tuần tới.
  - `GET /housekeeping/shift-registration/my-status`.

---

### Task 4: Frontend - Connect Admin Shift Management to Real DB & Approval

**Files:**
- Modify: `frontend/src/app/core/models/admin-staff.model.ts`
- Modify: `frontend/src/app/core/services/admin-staff.service.ts`
- Modify: `frontend/src/app/admin/staff-management/staff-management.component.ts`
- Modify: `frontend/src/app/admin/staff-management/staff-management.component.html`

- [ ] **Step 1: Bổ sung interface và methods trong `AdminStaffService`**
  - `updateScheduleCell(payload: { staffId: number, date: string, shiftType: string, note?: string })`
  - `generateAiRoster(startDate: string)`
  - `getPendingWeeklyRegistrations()`
  - `processWeeklyRegistration(registrationId: number, approved: boolean, reason?: string)`

- [ ] **Step 2: Cập nhật `saveCellShift()` và `saveNewShift()` trong `StaffManagementComponent`**
  - Khi người dùng chọn ca mới trong modal và bấm Lưu -> gọi `adminStaffService.updateScheduleCell(...)`.
  - Sau khi lưu thành công, hiển thị toast và làm mới bảng hoặc cập nhật cell state.

- [ ] **Step 3: Cập nhật `runAiRoster()` để lưu trực tiếp vào backend DB**
  - Gọi `adminStaffService.generateAiRoster(this.currentWeekStartDate)` -> backend lưu DB -> reload roster.

- [ ] **Step 4: Thêm giao diện Phê Duyệt Đơn Đăng Ký Ca Tuần trong `staff-management.component.html`**
  - Thêm section/card danh sách "Đơn Đăng Ký Ca Tuần Chờ Phê Duyệt" (Weekly Shift Registrations Pending Approval).
  - Hiển thị đầy đủ: Tên nhân viên, phòng ban, tuần đăng ký, 7 ngày ca trực đã chọn (với badge màu sắc trực quan: Sáng, Chiều, Đêm, Nghỉ), ghi chú và khu vực ưu tiên.
  - Cung cấp nút:
    - **"Phê Duyệt Lịch Tuần"**: Bấm để duyệt đơn, tự động đồng bộ vào bảng phân ca tuần.
    - **"Từ Chối"**: Mở modal/input nhập lý do từ chối.

---

### Task 5: Frontend - Connect Staff Housekeeping Portal Registration

**Files:**
- Modify: `frontend/src/app/core/services/housekeeping-mobile.service.ts`
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.ts`
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.html`

- [ ] **Step 1: Bổ sung API `submitShiftRegistration` và `getMyShiftRegistration` vào `HousekeepingMobileService`**
- [ ] **Step 2: Cập nhật `submitShiftRegistration()` trong `HousekeepingPortalComponent`**
  - Gửi dữ liệu đăng ký 7 ngày lên backend thay vì chỉ lưu `localStorage`.
  - Hiển thị trạng thái "Đang chờ Quản lý duyệt" hoặc "Đã được Quản lý duyệt" theo thời gian thực từ database.

---

### Task 6: Verification & End-to-End Testing

- [ ] **Step 1: Kiểm tra build backend bằng Maven**
  Run: `mvn compile` hoặc `mvn test-compile`.
- [ ] **Step 2: Kiểm tra build frontend**
- [ ] **Step 3: Chạy thử luồng thực tế (End-to-End)**:
  1. Mở Housekeeping Portal -> Đăng ký ca tuần mới 7 ngày -> Bấm gửi.
  2. Mở Admin Quản lý nhân sự -> Kiểm tra xem đơn đăng ký có xuất hiện trong danh sách "Đơn Đăng Ký Ca Tuần Chờ Phê Duyệt".
  3. Admin bấm "Phê Duyệt Lịch Tuần" -> Kiểm tra 7 ca trực được đẩy thẳng vào Bảng phân ca tuần.
  4. Admin click vào 1 ô ca trực -> Sửa thành "Ca Đêm" -> Tải lại trang (F5) -> Kiểm tra dữ liệu được lưu vĩnh viễn trong Database.
