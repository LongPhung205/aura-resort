# Thiết Kế Chi Tiết: Phân Hệ Quản Lý Tài Khoản Cho Admin (Unified Account Management Hub)

- **Ngày tạo:** 2026-09-21
- **Trạng thái:** Đã thống nhất thiết kế (Approved)
- **Tác giả:** Antigravity AI & Quản trị viên dự án

---

## 1. Mục Tiêu & Bối Cảnh (Context & Goals)
Dự án Khách Sạn Phú Quốc Sanctuary hiện đã hoàn thiện các phân hệ quản lý: Đặt phòng, Phòng & Hạng phòng, Dịch vụ, Khuyến mãi, Thanh toán, Đánh giá, và Nhân sự - Ca trực. Tuy nhiên, Admin hiện chưa có một trung tâm chỉ huy tập trung để quản lý toàn bộ tài khoản người dùng (`User`) trong cơ sở dữ liệu.

Phân hệ **Quản Lý Tài Khoản** (`/admin/users`) được xây dựng nhằm mục đích:
1. Quản lý toàn diện tài khoản Khách hàng (Hội viên VIP) và Nhân viên nội bộ trên một giao diện thống nhất, chuyên nghiệp.
2. Cung cấp đầy đủ các quyền năng quản trị: Tìm kiếm đa tiêu chí, Lọc theo vai trò/trạng thái, Khóa/Mở khóa tài khoản, Đặt lại mật khẩu an toàn, Cập nhật vai trò/thông tin cá nhân, Thêm mới tài khoản.
3. Liên kết hồ sơ khách hàng với lịch sử đơn đặt phòng (`bookings`) và chi tiêu thực tế.
4. Đảm bảo an toàn hệ thống (bảo vệ tài khoản root Admin, mã hóa BCrypt, kiểm tra tính duy nhất của email/số điện thoại).

---

## 2. Kiến Trúc Dữ Liệu & Backend REST API

### 2.1. Thực thể liên quan (`User` Entity)
- Đã tồn tại trong `com.phungvanlong.booking_hotel.entity.User`:
  - `id`: Long (Primary Key)
  - `email`: String (Unique, not null)
  - `password`: String (BCrypt hashed)
  - `fullName`: String (not null)
  - `phone`: String (Unique/indexed)
  - `role`: Role (`ROLE_CUSTOMER`, `ROLE_STAFF`, `ROLE_RECEPTIONIST`, `ROLE_BUTLER`, `ROLE_HOUSEKEEPING`, `ROLE_ACCOUNTANT`, `ROLE_ADMIN`)
  - `isActive`: Boolean (Mặc định `true`)
  - `provider`: AuthProvider (`LOCAL`, `GOOGLE`)
  - `bookings`: List<Booking> (Cascade Lazy)
  - `createdAt`, `updatedAt`: Kế thừa từ `BaseEntity`

### 2.2. Các DTOs Mới (`com.phungvanlong.booking_hotel.dto.user.*`)
1. **`UserResponseDto`**:
   - `id`: Long
   - `email`: String
   - `fullName`: String
   - `phone`: String
   - `role`: Role
   - `isActive`: Boolean
   - `provider`: AuthProvider
   - `createdAt`: LocalDateTime
   - `totalBookings`: Integer (Tổng số đơn đặt phòng)
   - `totalSpent`: Double (Tổng tiền đã thanh toán thành công)
   - `lastBookingDate`: LocalDateTime (Thời điểm đặt phòng gần nhất)
2. **`UserSummaryStatsDto`**:
   - `totalUsers`: Long (Tổng số người dùng)
   - `totalCustomers`: Long (Khách hàng có `role = ROLE_CUSTOMER`)
   - `totalStaff`: Long (Nhân sự nội bộ có `role != ROLE_CUSTOMER`)
   - `totalLocked`: Long (Số tài khoản có `isActive = false`)
3. **`CreateUserAdminRequest`**:
   - `email`: String (Not blank, valid email format)
   - `password`: String (Min 6 chars)
   - `fullName`: String (Not blank)
   - `phone`: String (Optional, regex 9-12 digits)
   - `role`: Role (Not null)
4. **`UpdateUserAdminRequest`**:
   - `fullName`: String (Not blank)
   - `phone`: String
   - `role`: Role
5. **`ResetPasswordAdminRequest`**:
   - `newPassword`: String (Min 6 chars)
6. **`UserFilterRequest`**:
   - `tab`: String (`CUSTOMER`, `STAFF`, hoặc `ALL`)
   - `search`: String (Tìm theo tên, email, sđt)
   - `role`: Role (Tùy chọn)
   - `isActive`: Boolean (Tùy chọn)
   - `page`: int (Mặc định 0)
   - `size`: int (Mặc định 10)

### 2.3. Lớp Xử Lý Nghiệp Vụ (`AdminUserService`)
- `Page<UserResponseDto> getUsers(UserFilterRequest filter, Pageable pageable)`:
  - Nếu `tab = CUSTOMER`: Lọc `role = ROLE_CUSTOMER`.
  - Nếu `tab = STAFF`: Lọc `role != ROLE_CUSTOMER`.
  - Hỗ trợ tìm kiếm không phân biệt hoa thường theo `fullName`, `email`, `phone`.
  - Hỗ trợ lọc theo `role` cụ thể và `isActive`.
- `UserSummaryStatsDto getStats()`:
  - Tính tổng các chỉ số cho Metric Cards.
- `UserResponseDto getUserById(Long id)`:
  - Trả về chi tiết kèm thông tin lịch sử đơn đặt.
- `UserResponseDto createUser(CreateUserAdminRequest request)`:
  - Kiểm tra trùng lặp email và số điện thoại.
  - Mã hóa password bằng `PasswordEncoder` (BCrypt).
  - Khởi tạo mặc định `isActive = true`, `provider = LOCAL`.
- `UserResponseDto updateUser(Long id, UpdateUserAdminRequest request)`:
  - Cập nhật `fullName`, `phone`, và `role`.
- `void toggleUserStatus(Long id, String currentAdminEmail)`:
  - Kiểm tra nếu `id` là tài khoản của chính Admin đang thao tác (`currentAdminEmail`), ném `BadRequestException` ("Không thể tự khóa tài khoản của chính mình").
  - Đảo ngược giá trị `isActive = !isActive`.
- `void resetPassword(Long id, ResetPasswordAdminRequest request)`:
  - Mã hóa mật khẩu mới bằng BCrypt và cập nhật cho `User`.

### 2.4. Danh Sách REST Endpoints (`AdminUserController`)
Bảo vệ toàn bộ controller bằng `@PreAuthorize("hasRole('ADMIN')")`. Prefix: `/api/v1/admin/users`.

| HTTP Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/v1/admin/users` | Lấy danh sách tài khoản có phân trang và bộ lọc |
| `GET` | `/api/v1/admin/users/stats` | Lấy thống kê số lượng tài khoản (Metric Cards) |
| `GET` | `/api/v1/admin/users/{id}` | Lấy chi tiết thông tin và lịch sử đặt phòng của 1 user |
| `POST` | `/api/v1/admin/users` | Tạo mới tài khoản khách hàng hoặc nhân viên |
| `PUT` | `/api/v1/admin/users/{id}` | Cập nhật thông tin cơ bản & vai trò |
| `PATCH` | `/api/v1/admin/users/{id}/status` | Khóa hoặc Kích hoạt tài khoản (Toggle `isActive`) |
| `PATCH` | `/api/v1/admin/users/{id}/reset-password` | Đặt lại mật khẩu mới |

---

## 3. Thiết Kế Giao Diện Frontend Angular & Tương Tác

### 3.1. Cấu Trúc File & Module
- **Service**: `frontend/src/app/core/services/admin-user.service.ts`
- **Component**: `frontend/src/app/admin/user-management/user-management.component.ts`, `.html`, `.css`
- **Route**: Khai báo route `/admin/users` trong `frontend/src/app/app.routes.ts`
- **Sidebar**: Cập nhật menu trong `frontend/src/app/admin/layout/admin-layout.component.html` trỏ tới `/admin/users` với icon `manage_accounts` và nhãn `Quản Lý Tài Khoản`.

### 3.2. Bố Cục UI (Layout & Luxury Styling)
1. **Header & 4 Metric Cards**:
   - `Tổng Tài Khoản`: Icon `groups`, màu xanh dương (#0284c7).
   - `Khách Hàng & VIP`: Icon `person`, màu ngọc lục bảo (emerald-600).
   - `Nhân Sự Nội Bộ`: Icon `badge`, màu tím (purple-600).
   - `Đang Bị Khóa`: Icon `lock_clock`, màu hổ phách/đỏ (rose-600).
2. **Tab Switcher (2 Tab)**:
   - `Tab 1: Khách Hàng & Hội Viên VIP` (Hiển thị các tài khoản khách, đơn đặt, mức chi tiêu).
   - `Tab 2: Đội Ngũ Nhân Sự & Ban Quản Trị` (Hiển thị nhân viên lễ tân, butler, buồng phòng, kế toán, admin).
3. **Thanh Tìm Kiếm & Bộ Lọc Nhanh**:
   - Search input debounce 300ms (Tìm theo họ tên, email, sđt).
   - Filter dropdown chọn Vai trò (`Role`).
   - Filter dropdown chọn Trạng thái (`Hoạt động` / `Đã khóa`).
   - Nút hành động chính: `+ Tạo Tài Khoản Mới`.
4. **Bảng Dữ Liệu (Data Table)**:
   - Avatar chữ cái đầu với dải màu gradient sang trọng.
   - Cột: Người dùng (Tên & Email), Số điện thoại, Vai trò (Badge chuẩn màu theo từng vai trò), Ngày tạo, Trạng thái (Hoạt động / Khóa), Thao tác.
   - Phân trang dưới đáy bảng (Page index, Page size, Prev/Next, Total items).
5. **Modals Tương Tác**:
   - `Modal Tạo Mới Tài Khoản`: Form nhập Họ tên, Email, Mật khẩu, Số điện thoại, Chọn Vai trò.
   - `Modal Chỉnh Sửa Thông Tin & Vai Trò`: Cập nhật thông tin nhanh.
   - `Modal Đặt Lại Mật Khẩu`: Nhập mật khẩu mới, kiểm tra độ dài.
   - `Modal Xem Chi Tiết & Lịch Sử Đơn Đặt`: Hiển thị danh sách booking gần đây của khách.
   - `Dialog Xác Nhận Khóa / Mở Khóa`: Cảnh báo trước khi khóa tài khoản.

---

## 4. An Toàn & Bảo Mật (Security & Error Handling)
1. **Self-lockout Prevention**: Ngăn Admin tự khóa hoặc tự tước quyền của chính mình.
2. **BCrypt Hashing**: 100% mật khẩu được băm an toàn, không lộ mật khẩu ra API response.
3. **Unique Constraints**: Bắt lỗi trùng email/phone và trả về thông điệp tiếng Việt thân thiện.
4. **Data Isolation**: Dữ liệu tải trực tiếp từ DB thực qua REST API, không sử dụng dữ liệu giả (mock data).

---

## 5. Kế Hoạch Xác Thực (Verification Plan)
1. **Backend Verification**:
   - Biên dịch Spring Boot thành công: `mvnw.cmd compile -q`.
   - Kiểm tra các REST endpoint bằng Postman hoặc curl / integration test.
2. **Frontend Verification**:
   - Biên dịch Angular thành công: `npm run build`.
   - Test trực quan trên trình duyệt (`http://localhost:4200/admin/users`):
     - Kiểm tra render bảng và 4 thẻ Metric.
     - Kiểm tra chuyển tab Khách hàng <-> Nhân sự.
     - Thử tìm kiếm và lọc vai trò.
     - Tạo mới 1 tài khoản nhân viên.
     - Thử khóa / mở khóa tài khoản vừa tạo.
     - Thử đổi mật khẩu cho tài khoản.
     - Kiểm tra liên kết từ Sidebar Admin.
