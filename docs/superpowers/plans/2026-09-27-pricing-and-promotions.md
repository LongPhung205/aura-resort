# Pricing & Promotions Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Hoàn thiện tính năng Quản lý Khuyến mãi (Promotions) và Giá động (Yield Pricing), kết nối đồng bộ giữa Backend và Frontend, thay thế các luồng mock bằng dữ liệu thực tế.

**Architecture:** Mở rộng các REST API trong `PromotionController` để hỗ trợ đầy đủ CRUD. Tái cấu trúc logic tính Giá động (Yield Management) trong `AdminYieldServiceImpl` để áp dụng thực tế các `YieldRule` từ Database thay vì hardcode 20%. Trên Frontend, tích hợp API thực tế vào `PromotionManagementComponent`, đồng thời sửa lỗi mapping dữ liệu (ví dụ: `roomTypes` vs `villaTypes`).

**Tech Stack:** Spring Boot, JPA/Hibernate, Angular 18, Tailwind CSS, Taiga UI

## Global Constraints

- Backend sử dụng Java 17, Spring Boot 3.x, MySQL.
- Frontend sử dụng Angular 18 (Standalone Components).
- Tuân thủ nguyên tắc TDD và YAGNI.
- Các API trả về chuẩn format `ApiResponse<T>`.

---

### Task 1: Mở rộng Promotion Entity & Controller (Backend)

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\entity\Promotion.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\dto\request\PromotionRequest.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\dto\response\PromotionResponse.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\PromotionServiceImpl.java`
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\controller\PromotionController.java`

**Interfaces:**
- Consumes: Existing Promotion entity.
- Produces: API `PUT /promotions/{id}`, `DELETE /promotions/{id}`. `Promotion` entity có thêm các trường: `name` (String), `category` (String - SUMMER, HONEYMOON, WELLNESS, ELITE).

- [ ] **Step 1: Cập nhật Entity và DTO**
Thêm trường `name` và `category` vào `Promotion.java`. Cập nhật `PromotionRequest` và `PromotionResponse` để map 2 trường này. Thêm validate cho request.

- [ ] **Step 2: Thêm method vào Service**
Thêm `updatePromotion(Long id, PromotionRequest request)` và `deletePromotion(Long id)` vào `PromotionService` và `PromotionServiceImpl`.

- [ ] **Step 3: Thêm endpoints vào Controller**
Cập nhật `PromotionController.java` thêm phương thức `PUT /{id}` và `DELETE /{id}`.

- [ ] **Step 4: Commit**
```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Promotion.java
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/PromotionServiceImpl.java
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/PromotionController.java
git commit -m "feat(backend): add update and delete endpoints for promotions and extra fields"
```

---

### Task 2: Tích hợp API Khuyến mãi thực tế vào Frontend

**Files:**
- Modify: `d:\booking_hotel\frontend\src\app\core\services\admin-yield.service.ts`
- Modify: `d:\booking_hotel\frontend\src\app\admin\promotion-management\promotion-management.component.ts`
- Modify: `d:\booking_hotel\frontend\src\app\admin\promotion-management\promotion-management.component.html`

**Interfaces:**
- Consumes: API `/promotions`
- Produces: Giao diện quản lý khuyến mãi hiển thị dữ liệu thật, hỗ trợ tạo mới và xóa thông qua API.

- [ ] **Step 1: Cập nhật AdminYieldService**
Thêm các hàm `createPromotion(data)`, `updatePromotion(id, data)`, `deletePromotion(id)`.

- [ ] **Step 2: Gọi API thực tế khi Save**
Trong `promotion-management.component.ts`, sửa đổi `saveNewPromo()` để gọi `AdminYieldService.createPromotion`. Sau khi thành công thì gọi `loadPromotions()` và ẩn modal.
Sửa đổi logic `loadPromotions()` để map các trường `name`, `category` từ Backend (không hardcode `category` từ logic `code.includes('SUMMER')` nữa).

- [ ] **Step 3: Cập nhật Quick Coupon**
Sửa đổi `issueQuickCoupon()` để gọi API tạo promotion với loại ELITE.

- [ ] **Step 4: Commit**
```bash
git add frontend/src/app/core/services/admin-yield.service.ts
git add frontend/src/app/admin/promotion-management
git commit -m "feat(frontend): integrate real promotion APIs for CRUD operations"
```

---

### Task 3: Logic tính toán Giá động (Yield Management) dựa trên Rules

**Files:**
- Modify: `d:\booking_hotel\backend\booking_hotel\src\main\java\com\phungvanlong\booking_hotel\service\impl\AdminYieldServiceImpl.java`

**Interfaces:**
- Consumes: `YieldRule` từ Database, `VillaType` và `RoomType`.
- Produces: Tính toán `dynamicPrice` của `VillaType` và `RoomType` dựa trên `occupancyRate` thay vì cộng cứng 20%.

- [ ] **Step 1: Tái cấu trúc AdminYieldServiceImpl**
Trong hàm `applyDynamicPricing()`, lấy danh sách các `YieldRule` đang active (`isActive = true`). Tính toán `currentOccupancy` của hệ thống. So sánh `currentOccupancy` với `rule.getThresholdValue()` để xác định có áp dụng quy tắc này không (ví dụ `conditionType = OCCUPANCY_THRESHOLD`).
Tính `multiplier` lớn nhất hoặc tích lũy. Nếu không có rule nào thỏa mãn, dùng `1.0`.

- [ ] **Step 2: Áp dụng Multiplier vào VillaType/RoomType**
Cập nhật `dynamicPrice = basePrice * multiplier` cho tất cả các loại phòng, sau đó lưu lại vào DB.

- [ ] **Step 3: Commit**
```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/AdminYieldServiceImpl.java
git commit -m "feat(backend): calculate dynamic pricing based on active YieldRules"
```

---

### Task 4: Sửa lỗi Mapping Ma trận giá (Rate Matrix) trên Frontend

**Files:**
- Modify: `d:\booking_hotel\frontend\src\app\admin\promotion-management\promotion-management.component.ts`
- Modify: `d:\booking_hotel\frontend\src\app\core\models\admin-yield.model.ts` (Nếu có)

**Interfaces:**
- Consumes: API `/admin/yield/rate-matrix` (trả về danh sách `villaTypes`)
- Produces: Bảng ma trận giá hiển thị chính xác các biệt thự.

- [ ] **Step 1: Cập nhật Interface Model**
Kiểm tra `YieldMatrixResponse` trong frontend, đổi `roomTypes` thành `villaTypes` để match với backend `AdminYieldServiceImpl` (trả về `villaTypes`).

- [ ] **Step 2: Cập nhật hàm loadRateMatrix**
Trong `promotion-management.component.ts`, đổi `matrix.roomTypes` thành `matrix.villaTypes`. Map các thuộc tính `villaTypeName` thành `name`.

- [ ] **Step 3: Commit**
```bash
git add frontend/src/app/admin/promotion-management/promotion-management.component.ts
git commit -m "fix(frontend): map villaTypes array instead of roomTypes for Rate Matrix"
```
