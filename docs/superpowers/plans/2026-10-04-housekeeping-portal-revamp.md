# Housekeeping Portal Revamp Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Hoàn thiện và nâng cấp toàn diện giao diện trang Quản lý Buồng phòng (/housekeeping) thành Cổng Điều Hành Buồng Phòng Đẳng Cấp 5 Sao (Aura Housekeeping Operations Portal) với logo thương hiệu đầy đủ, sidebar chuẩn ERP, KPI trực quan và responsive tối ưu.

**Architecture:** Giữ nguyên kiến trúc Standalone Component Angular của `HousekeepingPortalComponent`, nâng cấp toàn bộ template HTML và SCSS với hệ thống nhận diện thương hiệu chuẩn Aura Villas (`/logo.svg`, màu xanh luxury `#0284c7`, slate `#0f172a`, card bóng mờ tinh tế), bổ sung breadcrumbs, đồng hồ số realtime, thanh tác vụ điều hành và thẻ phòng 5★.

**Tech Stack:** Angular 17+ (Standalone Components, Signals / Observables, RxJS), Tailwind CSS, Material Symbols Outlined, TypeScript.

---

### Task 1: Nâng cấp Header Điều Hành Đẳng Cấp 5★ với Logo Website và Thông Tin Ca Trực

**Files:**
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.html:1-115`
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.ts`
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.scss`

**Interfaces:**
- Consumes: `staffName`, `staffRole`, `staffShift`, `staffAvatar`, `isAdmin`, `currentTime`
- Produces: Top Navigation Header có Logo `/logo.svg`, breadcrumb `Trang chủ > Quản lý buồng phòng`, đồng hồ số sống động, cụm nút Báo sự cố, Đồng bộ, Về trang chủ, Admin và Profile đăng xuất.

- [ ] **Step 1: Cập nhật component TS để hỗ trợ ngày giờ thực tế & mở rộng thông tin**
- [ ] **Step 2: Cập nhật template Header trong `housekeeping-portal.component.html`**
- [ ] **Step 3: Bổ sung style SCSS cho Header phát sáng nhẹ và transition mượt mà**
- [ ] **Step 4: Kiểm tra hiển thị và kiểm tra lỗi TypeScript với `npx tsc --noEmit`**

---

### Task 2: Tái thiết kế Sidebar Điều Hành Chuẩn Hospitality ERP

**Files:**
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.html:116-160`
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.ts`

**Interfaces:**
- Consumes: `activeMenu`, `totalAssignedCount`, `dirtyRooms.length`, `rushCount`
- Produces: Sidebar phân nhóm khoa học (Tác nghiệp dọn phòng, Lịch làm việc ca trực, Nghỉ phép & OT, Sổ sự cố), có badge đếm số lượng realtime, hỗ trợ đóng/mở trên mobile.

- [ ] **Step 1: Cập nhật HTML Sidebar với 3 phân nhóm rõ ràng và badges đếm số lượng**
- [ ] **Step 2: Thêm nút chuyển đổi hiển thị sidebar trên màn hình di động/tablet**
- [ ] **Step 3: Kiểm tra compile `npx tsc --noEmit`**

---

### Task 3: Tối ưu Bảng Điều Khiển Tác Vụ, 5 Thẻ KPI & Bộ Lọc Phòng 5★

**Files:**
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.html:160-500`
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.scss`

**Interfaces:**
- Consumes: `shiftCompletionRate`, `completedCount`, `totalAssignedCount`, `rushCount`, `filteredMyTasks`, `dirtyRooms`
- Produces: Thanh tiến độ ca trực cao cấp, 5 KPI cards viền kim loại sang trọng, bộ lọc phòng và thanh tìm kiếm đa năng.

- [ ] **Step 1: Nâng cấp thanh tiến độ ca trực Ribbon với hiệu ứng ánh sáng shimmer**
- [ ] **Step 2: Cải tiến 5 thẻ KPI có icon màu đồng bộ và chỉ số rõ nét**
- [ ] **Step 3: Tinh chỉnh thanh tìm kiếm và các tab trạng thái phòng**
- [ ] **Step 4: Kiểm tra compile `npx tsc --noEmit`**

---

### Task 4: Hoàn thiện Thẻ Biệt Thự / Phòng Dọn Dẹp & Trải Nghiệm Tác Nghiệp

**Files:**
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.html:500-1188`
- Modify: `frontend/src/app/features/housekeeping/housekeeping-portal.component.ts`

**Interfaces:**
- Consumes: `HousekeepingTask`, `openWorkspace()`, `startTask()`, `openIncidentModal()`
- Produces: Thẻ phòng dọn dẹp chuẩn 5 sao có đầy đủ ảnh villa, trạng thái thẻ từ NFC, nút bấm tác vụ nhanh, modal báo sự cố và modal mô phỏng NFC.

- [ ] **Step 1: Cải tiến bố cục thẻ phòng dọn dẹp với hình ảnh, tag phân khu, giờ dự kiến đến của khách**
- [ ] **Step 2: Chuẩn hóa các nút tác vụ (Bắt đầu dọn, Ozone, Mở workspace kiểm tra)**
- [ ] **Step 3: Kiểm tra tổng thể với `npx tsc --noEmit`**
