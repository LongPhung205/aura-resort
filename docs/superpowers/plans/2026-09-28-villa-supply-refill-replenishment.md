# Kế Hoạch Triển Khai: Hệ Thống Định Mức, Kiểm Kê Sau Checkout & Tự Động Hóa Refill Villa

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Xây dựng luồng nghiệp vụ hoàn chỉnh cho Kho & Vật tư Aura Resort theo chu trình: **Định mức tiêu chuẩn Villa $\rightarrow$ Kiểm kê sau checkout $\rightarrow$ Tự động tính số lượng bổ sung $\rightarrow$ Tạo Refill Task $\rightarrow$ Xuất kho trung tâm $\rightarrow$ Cập nhật tồn kho Villa $\rightarrow$ Hoàn tất sẵn sàng đón khách.**

**Architecture:** Mở rộng hệ thống bằng 4 thực thể cốt lõi (`VillaSupplyStandard`, `VillaInventory`, `RefillTask`, `RefillTaskItem`), liên kết chặt chẽ giữa `HousekeepingTask` (dọn dẹp/kiểm kê), `InventoryItem` (kho trung tâm), `InventoryTransaction` (nhật ký xuất cấp phát) và `Villa`. Phía frontend mở rộng giao diện `/admin/inventory` với các tab: Quản lý Refill, Định mức tiêu chuẩn theo Villa, và Tồn kho thực tế tại từng Villa kèm nút thao tác 1 chạm **"[Xuất Kho & Hoàn Tất]"**.

**Tech Stack:** Spring Boot 3.x, Spring Data JPA, Hibernate, MySQL 8.0, Angular 17+ Standalone, Tailwind CSS.

## Global Constraints
- Giữ nguyên toàn bộ tài khoản người dùng và dữ liệu đã khởi tạo trong database `hotel_booking_db`.
- Mọi API admin được cấu hình dưới tiền tố `/api/v1/admin/inventory/refill/**`.
- Phong cách UI Tailwind CSS sang trọng, sạch sẽ, chuẩn resort 5 sao ("ponytail" guidelines, Material Symbols icons, không emoji lộn xộn).
- Quy tắc tính số lượng Refill: $\text{Refill} = \max(0, \text{Standard} - \text{Actual})$.
- Khi hoàn tất Refill: tự động trừ tồn kho trung tâm (`InventoryItem.inStock`), đồng thời lưu `InventoryTransaction` với type `EXPORT`.

---

## Danh Sách File Cần Tạo & Chỉnh Sửa

### Backend (`backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/`):
- `entity/RefillTaskStatus.java` (Enum: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`)
- `entity/VillaSupplyStandard.java` (Định mức chuẩn từng món cho từng Villa)
- `entity/VillaInventory.java` (Số lượng vật tư thực tế hiện diện tại từng Villa)
- `entity/RefillTask.java` (Nhiệm vụ bổ sung vật tư sau checkout/kiểm tra)
- `entity/RefillTaskItem.java` (Chi tiết từng món: định mức, còn lại, cần bổ sung, đã dùng, hỏng, mất)
- `repository/VillaSupplyStandardRepository.java`
- `repository/VillaInventoryRepository.java`
- `repository/RefillTaskRepository.java`
- `repository/RefillTaskItemRepository.java`
- `dto/request/VillaSupplyStandardRequest.java`
- `dto/request/CreateRefillTaskRequest.java`
- `dto/request/RefillItemCheckRequest.java`
- `dto/response/VillaSupplyStandardResponse.java`
- `dto/response/VillaInventoryResponse.java`
- `dto/response/RefillTaskResponse.java`
- `dto/response/RefillTaskItemResponse.java`
- `service/AdminRefillService.java`
- `service/impl/AdminRefillServiceImpl.java`
- `controller/AdminRefillController.java`
- `config/DataInitializer.java` (Seed định mức chuẩn và 1 Refill Task mẫu)
- `src/test/java/.../service/RefillServiceTest.java` (Unit & Integration tests)

### Frontend (`frontend/src/app/`):
- `core/models/refill.model.ts`
- `core/services/admin-refill.service.ts`
- `admin/inventory-management/inventory-management.component.ts`
- `admin/inventory-management/inventory-management.component.html`

---

## Chi Tiết Các Task Triển Khai

### Task 1: Backend Data Model, Enums & Repositories

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/RefillTaskStatus.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/VillaSupplyStandard.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/VillaInventory.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/RefillTask.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/RefillTaskItem.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/VillaSupplyStandardRepository.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/VillaInventoryRepository.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/RefillTaskRepository.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/RefillTaskItemRepository.java`

- [x] **Step 1: Tạo Enum `RefillTaskStatus.java`**
- [x] **Step 2: Tạo Entity `VillaSupplyStandard.java`**
- [x] **Step 3: Tạo Entity `VillaInventory.java`**
- [x] **Step 4: Tạo Entity `RefillTask.java` & `RefillTaskItem.java`**
- [x] **Step 5: Tạo Repositories**
- [x] **Step 6: Commit Task 1**

---

### Task 2: Backend DTOs & Service Layer

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/VillaSupplyStandardRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/CreateRefillTaskRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/RefillItemCheckRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/VillaSupplyStandardResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/VillaInventoryResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/RefillTaskResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/RefillTaskItemResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/AdminRefillService.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/AdminRefillServiceImpl.java`

- [x] **Step 1: Tạo DTOs Request & Response**
- [x] **Step 2: Xây dựng `AdminRefillService` & `AdminRefillServiceImpl`**
- [x] **Step 3: Commit Task 2**

---

### Task 3: Backend Controller, Seeding & Tests

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminRefillController.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/DataInitializer.java`
- Create: `backend/booking_hotel/src/test/java/com/phungvanlong/booking_hotel/service/RefillServiceTest.java`

- [x] **Step 1: Viết `RefillServiceTest.java`**
- [x] **Step 2: Viết `AdminRefillController.java`**
- [x] **Step 3: Cập nhật `DataInitializer.java`**
- [x] **Step 4: Chạy kiểm thử Maven**
- [x] **Step 5: Commit Task 3**

---

### Task 4: Frontend Service & Model Integration

**Files:**
- Create: `frontend/src/app/core/models/refill.model.ts`
- Create: `frontend/src/app/core/services/admin-refill.service.ts`

- [x] **Step 1: Tạo `refill.model.ts`**
- [x] **Step 2: Tạo `AdminRefillService`**
- [x] **Step 3: Kiểm tra TypeScript compilation**
- [x] **Step 4: Commit Task 4**

---

### Task 5: Frontend UI Redesign — Quản Lý Refill, Định Mức & Tồn Kho Villa

**Files:**
- Modify: `frontend/src/app/admin/inventory-management/inventory-management.component.ts`
- Modify: `frontend/src/app/admin/inventory-management/inventory-management.component.html`

- [x] **Step 1: Cập nhật component TS**
- [x] **Step 2: Cập nhật component HTML với phong cách Luxury Tailwind**
- [x] **Step 3: Kiểm thử giao diện và Format mã nguồn**
- [x] **Step 4: Commit Task 5**

---

## Hướng Dẫn Nghiệm Thu (Verification Guide)

1. **Backend Tests**: `.\mvnw.cmd test "-Dtest=RefillServiceTest"` $\rightarrow$ Đảm bảo 100% tests Passed.
2. **Kiểm tra API Live**:
   - `GET http://localhost:8080/api/v1/admin/inventory/refill/standards?villaId=1`
   - `GET http://localhost:8080/api/v1/admin/inventory/refill/tasks`
   - `POST http://localhost:8080/api/v1/admin/inventory/refill/tasks/{id}/fulfill`
3. **Kiểm tra UI**: Truy cập `http://localhost:4200/admin/inventory`:
   - Chuyển sang Tab "Nhiệm Vụ Refill Villa", bấm nút **[ Xuất Kho & Hoàn Tất ]** trên phiếu chờ xử lý.
   - Quan sát số lượng tồn kho trung tâm tự động giảm đi đúng bằng lượng đã refill, tab Tồn kho Villa cập nhật đủ 100%, và tab Nhật ký kho xuất hiện ngay 1 giao dịch `EXPORT`.
   - Bấm "+ Kiểm Kê Sau Checkout", chọn Villa, nhập số lượng còn lại, kiểm tra công thức tính tự động và tạo nhiệm vụ mới.
