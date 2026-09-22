# Thiết Kế Dọn Sạch Dữ Liệu Mẫu Hardcode & Chuẩn Hóa Trạng Thái Rỗng Trang Quản Lý Biệt Thự

**Ngày:** 2026-09-22  
**Mục tiêu:** Xóa sạch toàn bộ dữ liệu mẫu hardcode (mock data) ở cả tầng Frontend Angular và tầng Backend Database trên trang Quản lý Biệt Thự (`http://localhost:4200/admin/villas`), chuyển toàn bộ các chỉ số KPI sang tính toán động 100%, bổ sung giao diện Empty State 5 sao, cho phép người quản trị tự tay khởi tạo toàn bộ danh mục từ con số 0.

---

## 1. Bối Cảnh & Vấn Đề

Trang Quản lý Biệt Thự (`RoomManagementComponent`) hiện tại còn tồn tại một số thành phần dữ liệu mẫu hardcode:
1. **Frontend Mock Data:**
   - 5 thẻ KPI trên cùng chứa các con số và thanh phần trăm hardcode (`34`, `04`, `02`, `00`, `85%`).
   - Mảng `customZones` có sẵn 5 phân khu mẫu (`Khu A - Biển Đông`, `Khu B - Đầm Hoàng Hôn`, ...).
   - Mảng `roomTypes` có sẵn 4 hạng phòng / loại giường mẫu (`Grand Oceanfront`, `Sunset Lagoon`, ...).
   - Mảng `cleaningCards` có 4 thẻ tiến độ dọn buồng phòng mock cố định (`Grand Villa #802`, ...).
   - Dropdown chọn hạng phòng trong modal còn chứa các `<option *ngIf="roomTypesList.length === 0" [value]="17">...` tĩnh.
2. **Backend Database Seeding:**
   - `DataInitializer.java` tự động nạp 4 VillaType, 12 căn Villa vật lý, các phòng ngủ con bên trong villa và các đơn đặt phòng / nhiệm vụ dọn dẹp giả lập mỗi khi khởi động lại Spring Boot.

**Mong muốn:** Dọn sạch toàn bộ dữ liệu mẫu để hệ thống ở trạng thái trắng (0 biệt thự, 0 loại giường, 0 phân khu), hiển thị Empty State sang trọng và cho phép người quản lý tự tay thêm mới toàn bộ.

---

## 2. Kiến Trúc & Giải Pháp Chi Tiết

### 2.1. Backend (Spring Boot & MySQL)
1. **Vô hiệu hóa Auto-Seed trong `DataInitializer.java`:**
   - Tắt việc tự động gọi các phương thức khởi tạo dữ liệu mẫu:
     - `initVillaTypesAndVillas()`
     - `initBookingsAndDispatches()`
     - `initReviewsAndRecovery()`
   - **Bảo lưu:** Phương thức `initUsers()` để giữ lại tài khoản quản trị viên (`admin@auraholdings.vn` / `admin123`) và các nhân sự nội bộ, đảm bảo quy trình đăng nhập không bị gián đoạn.
2. **Dọn sạch Database qua câu lệnh SQL an toàn:**
   - Xóa dữ liệu có kiểm soát theo đúng thứ tự ràng buộc khóa ngoại (Foreign Key Constraints):
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

### 2.2. Frontend Component (`room-management.component.ts`)
1. **Xóa bỏ các mảng mock tĩnh:**
   - `customZones: CustomZoneItem[] = []`: Bắt đầu từ danh sách rỗng (đồng thời xóa khóa lưu tạm `aura_resort_custom_zones` trong `localStorage`).
   - `roomTypes: RoomTypeDisplayItem[] = []`: Rỗng ban đầu, chỉ hiển thị dữ liệu được tải về từ API backend.
   - `rooms: RoomCard[] = []`: Rỗng ban đầu.
   - `filteredRooms: RoomCard[] = []`: Rỗng ban đầu.
   - `cleaningCards = []`: Xóa bỏ 4 phần tử mock cứng; chỉ hiển thị khi có nhiệm vụ dọn dẹp thực tế từ `liveHousekeepingTasks`.
2. **Tính toán động 100% cho 5 thẻ KPI đầu trang:**
   - Cung cấp các getter / thuộc tính động:
     - `totalVillasCount`: `this.rooms.length`
     - `occupiedVillasCount`: Số lượng căn có `statusRaw === 'OCCUPIED'`
     - `availableVillasCount`: Số lượng căn có `statusRaw === 'AVAILABLE'`
     - `cleaningVillasCount`: Số lượng căn có `statusRaw === 'CLEANING'`
     - `maintenanceVillasCount`: Số lượng căn có `statusRaw === 'MAINTENANCE'`
     - `occupancyPercentage`: `totalVillasCount > 0 ? Math.round((occupiedVillasCount / totalVillasCount) * 100) : 0`
     - `availablePercentage`: `totalVillasCount > 0 ? Math.round((availableVillasCount / totalVillasCount) * 100) : 0`
3. **Làm sạch form modal thêm / sửa biệt thự & loại giường:**
   - Loại bỏ các `<option *ngIf="roomTypesList.length === 0">` hardcode.
   - Nếu `roomTypesList` chưa có phần tử, dropdown hiển thị hướng dẫn: `"Chưa có loại giường - Hãy bấm + Thêm Loại Giường trước"`.
   - Nếu `customZones` chưa có phần tử, dropdown hiển thị hướng dẫn: `"Chưa có phân khu - Hãy bấm + Thêm Phân Khu trước"`.

### 2.3. Frontend Template & Giao Diện Empty State (`room-management.component.html`)
1. **Thẻ KPI (Section 1):**
   - Ràng buộc trực tiếp với các giá trị động (`totalVillasCount`, `occupiedVillasCount`, v.v.).
   - Khi chưa có căn nào: hiển thị `0 căn`, `0% Công suất`, `0 Căn sạch`, `0 Đang xử lý`, `0 Căn ngưng dùng`.
2. **Khu vực Phân Khu (Section 2):**
   - Khi `customZones.length === 0`: Hiển thị thông báo hướng dẫn nhẹ nhàng: *"Chưa có phân khu nào được tạo. Bấm '+ Thêm Phân Khu' để phân loại các cụm biệt thự."*
3. **Khu vực Loại Giường (Section 3):**
   - Khi `roomTypes.length === 0`: Hiển thị Card Empty State sang trọng có viền đứt nét (border-dashed), icon `hotel`, tiêu đề *"Chưa Có Danh Mục Loại Giường"*, mô tả *"Hãy thiết lập các tiêu chuẩn quy cách giường và sức chứa cho resort của bạn."* kèm nút bấm `+ Thêm Loại Giường Đầu Tiên`.
4. **Khu vực Lưới Biệt Thự (Section 4):**
   - Khi `rooms.length === 0`: Hiển thị giao diện Empty State phong cách resort cao cấp gồm:
     - Biểu tượng `villa` lớn nổi bật.
     - Tiêu đề: *"Hệ Thống Chưa Có Căn Biệt Thự Nào"*.
     - Sơ đồ hướng dẫn 3 bước:
       - **Bước 1:** Thêm Loại Giường (Quy cách giường & sức chứa).
       - **Bước 2:** Thêm Phân Khu (Bờ biển, vách đá, đồi thông...).
       - **Bước 3:** Khởi tạo Biệt Thự Mới (Nhập mã hiệu và đơn giá thuê/đêm).
     - Nút bấm trực tiếp mở modal: `"Khởi Tạo Biệt Thự Đầu Tiên"`.

---

## 3. Kế Hoạch Xác Minh & Kiểm Thử
1. **Kiểm tra biên dịch:** Chạy `npm run build` hoặc `npx ng build` để đảm bảo 0 lỗi TypeScript và Template HTML.
2. **Kiểm tra trạng thái hiển thị rỗng:**
   - Mở trình duyệt tại `http://localhost:4200/admin/villas`.
   - Xác nhận 5 thẻ KPI hiển thị `0` căn, `0%`.
   - Xác nhận Section 3 hiển thị Empty State của Loại Giường.
   - Xác nhận Section 4 hiển thị Empty State quy trình 3 bước của Biệt Thự.
3. **Kiểm tra luồng thêm mới bằng tay:**
   - Bấm `+ Thêm Loại Giường` → Tạo 1 loại giường mới → Xác nhận lưu thành công và xuất hiện trên giao diện.
   - Bấm `+ Thêm Phân Khu` → Tạo 1 phân khu mới → Xuất hiện trên thanh phân khu.
   - Bấm `Khởi Tạo Biệt Thự Mới` → Chọn loại giường và phân khu vừa tạo → Nhập mã hiệu và đơn giá → Lưu thành công.
   - Xác nhận thẻ villa xuất hiện, KPI nhảy lên `1 căn`, đơn giá hiển thị chính xác.
