# Thiết Kế Chi Tiết: Nâng Cấp Form Khởi Tạo & Quản Lý Biệt Thự (Villa Management Redesign)

**Ngày lập:** 23/09/2026  
**Trạng thái:** Bản thảo thiết kế (Design Spec)  
**Tác giả:** Pair Programming (User & AI)  
**Phạm vi:** Frontend Angular (`room-management`) & Backend Spring Boot (`villas`, `rooms`, `villa-types`)

---

## 1. Bối Cảnh & Mục Tiêu

### 1.1. Hiện trạng
- Form "Khởi Tạo Biệt Thự Mới" hiện tại chỉ cho phép chọn **1 loại giường duy nhất** cho toàn bộ căn biệt thự (dùng chung cho mọi phòng ngủ con).
- Trường "Tầng lầu" hiện tại chỉ là số nguyên đơn thuần (`1, 2, 3`), chưa thể hiện được kết cấu kiến trúc đặc thù của biệt thự nghỉ dưỡng (ví dụ: *Villa 1 tầng, Villa 2 tầng, Duplex thông tầng, Triplex, Penthouse*).
- Tiêu chuẩn khử khuẩn Ozon hiện là dropdown đơn lẻ, chưa linh hoạt để cấu hình gói dịch vụ và tiện ích mở rộng đi kèm của từng căn biệt thự.

### 1.2. Mục tiêu đạt được
1. **Tích chọn nhiều loại giường linh hoạt trong 1 Villa (Lựa chọn 3 - Kết hợp):**
   - **Chế độ nhanh (Mặc định):** Bảng kiểm (Checklist) toàn bộ danh mục giường có trong resort kèm ô tăng/giảm số lượng (ví dụ: *1x King Size Bed + 1x Queen Size Bed + 2x Single Bed*). Tự động tính toán tổng số giường, số lượng phòng ngủ, và tổng sức chứa (Người lớn + Trẻ nhỏ) thời gian thực.
   - **Chế độ nâng cao (Tùy chọn mở rộng):** Cho phép phân bổ chi tiết các loại giường đã chọn vào từng phòng ngủ con cụ thể (ví dụ: *Phòng ngủ Master - Tầng 1*, *Phòng ngủ Phụ 1 - Tầng 2*...).
2. **Tự động xác định Hạng Biệt Thự:**
   - Hệ thống tự động phân loại căn thành `Villa 1 Phòng Ngủ`, `Villa 2 Phòng Ngủ`, `Villa 3 Phòng Ngủ`... dựa trên số lượng phòng cấu hình, không bắt người dùng phải nhập thủ công.
3. **Bảo toàn các trường thông tin cốt lõi:**
   - **Mã Villa / Tên định danh riêng:** Ví dụ `Villa 101`, `Villa 202`, `#805`.
   - **Phân khu nghỉ dưỡng (Zone):** Quản lý cụm địa lý (ví dụ: *Ngọc Trai*, *San Hô*, *Vách Đá*...).
   - **Trạng thái vận hành:** *Sẵn sàng đón khách (AVAILABLE)*, *Đang có khách (OCCUPIED)*, *Đang dọn dẹp (CLEANING)*, *Đang bảo trì (MAINTENANCE)*.
   - **Hình ảnh Biệt thự / Homestay:** Tải ảnh từ thư mục máy tính, xem trước ảnh sắc nét kèm nút xóa nhanh, và bộ chọn nhanh ảnh mẫu resort (*Biển Đông, Hoàng Hôn, Vách Đá, Dinh Thự VIP*).
4. **Chuẩn hóa kiến trúc & tiện ích:**
   - Đổi "Tầng lầu" thành **"Số Tầng / Kết Cấu"** (*1 Tầng, 2 Tầng, 3 Tầng, Duplex Thông Tầng, Triplex, Penthouse*).
   - Chuyển "Khử khuẩn Ozon" thành **"Dịch vụ đi kèm & Tiện ích mở rộng"** dạng checklist đa chọn (*Khử trùng Ozon định kỳ, Quản gia Lead Butler 24/7, Hồ bơi vô cực riêng, Miễn phí xe đạp nội khu, Bếp nấu cao cấp, Đón tiễn sân bay*).

---

## 2. Đặc Tả Giao Diện Người Dùng (UI/UX Specification)

### 2.1. Wireframe Modal "Khởi Tạo / Chỉnh Sửa Biệt Thự"
```
+------------------------------------------------------------------------------------------------+
| 🏠 KHỞI TẠO BIỆT THỰ MỚI                                                               [X]     |
| Thiết lập biệt thự nghỉ dưỡng, cấu trúc phòng ngủ và tiện ích dịch vụ                          |
+------------------------------------------------------------------------------------------------+
| [KHỐI 1: THÔNG TIN ĐỊNH DANH & KIẾN TRÚC]                                                      |
|                                                                                                |
|   Mã Số / Tên Villa (*)         Phân Khu Nghỉ Dưỡng (*)          Số Tầng / Kết Cấu (*)         |
|   [ Villa #101           ]     [ Phân Khu Ngọc Trai         ▼ ]  [ 2 Tầng (2 Floors)        ▼ ]|
|                                                                                                |
|   Đơn Giá Thuê / Đêm (VNĐ) (*)  Trạng Thái Vận Hành              Hạng Biệt Thự (Tự động)       |
|   [ 25,000,000         VNĐ ]   [ Sẵn Sàng Đón Khách         ▼ ]  [ ✨ Villa 3 Phòng Ngủ       ]|
|   ≈ 25.0 Triệu VNĐ / đêm                                                                       |
+------------------------------------------------------------------------------------------------+
| [KHỐI 2: CẤU HÌNH LOẠI GIƯỜNG & PHÒNG NGỦ - HYBRID MULTI-BED SELECTOR]                        |
|                                                                                                |
|   🌟 TỔNG HỢP: 3 Phòng Ngủ • 4 Chiếc Giường • 5 Người Lớn • 1 Trẻ Nhỏ • Tối Đa 6 Khách         |
|                                                                                                |
|   Danh mục loại giường trong căn:                                                              |
|   ┌────────────────────────────────────────────────────────────────────────────────────────┐   |
|   │ [x] King Size Bed   │ Rộng 1.8m × Dài 2.0m (2 NL • 0 TE)    │ Số lượng: [-] [ 1 ] [+]  │   |
|   │ [x] Queen Size Bed  │ Rộng 1.8m × Dài 2.0m (2 NL • 1 TE)    │ Số lượng: [-] [ 1 ] [+]  │   |
|   │ [x] Single Bed      │ Rộng 1.0m × Dài 1.9m (1 NL • 0 TE)    │ Số lượng: [-] [ 2 ] [+]  │   |
|   │ [ ] Super King      │ Rộng 2.0m × Dài 2.2m (3 NL • 1 TE)    │ Số lượng: [-] [ 0 ] [+]  │   |
|   └────────────────────────────────────────────────────────────────────────────────────────┘   |
|                                                                                                |
|   [⌄ Phân bổ chi tiết vào từng phòng ngủ con (Tùy chọn nâng cao)]                              |
|   ┌ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -┐   |
|   :  Phòng 1 (Tầng 1): [ Phòng Ngủ Master          ] ➔ 1x King Size Bed                    :   |
|   :  Phòng 2 (Tầng 2): [ Phòng Ngủ Phụ 1 - Hướng Biển ] ➔ 1x Queen Size Bed                 :   |
|   :  Phòng 3 (Tầng 2): [ Phòng Ngủ Phụ 2 - Twin Bed ] ➔ 2x Single Bed                      :   |
|   └ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -┘   |
+------------------------------------------------------------------------------------------------+
| [KHỐI 3: DỊCH VỤ ĐI KÈM & TIỆN ÍCH MỞ RỘNG]                                                    |
|   [x] Khử trùng Ozon định kỳ (Chuẩn 5*)           [x] Quản gia riêng Lead Butler 24/7          |
|   [x] Hồ bơi vô cực riêng tư                      [x] Miễn phí xe đạp nội khu                  |
|   [ ] Bếp nấu cao cấp & minibar miễn phí          [ ] Đón tiễn sân bay bằng xe riêng           |
|   [ ] Tiệc nướng BBQ & trà chiều sân vườn                                                      |
+------------------------------------------------------------------------------------------------+
| [KHỐI 4: HÌNH ẢNH ĐẠI DIỆN BIỆT THỰ / HOMESTAY]                                                |
|   - Tải ảnh từ thư mục máy tính (PNG, JPG, WEBP) kèm nút chọn tệp                              |
|   - Khung xem trước trực quan (Preview) sắc nét, nút đổi ảnh & gỡ ảnh                          |
|   - Ảnh mẫu resort: [Biển Đông] [Hoàng Hôn] [Vách Đá] [Dinh Thự VIP]                           |
+------------------------------------------------------------------------------------------------+
|                                                                     [ Hủy Bỏ ] [ Xác Nhận Lưu ]|
+------------------------------------------------------------------------------------------------+
```

---

## 3. Kiến Trúc Dữ Liệu & API Backend

### 3.1. Cơ sở dữ liệu MySQL
1. **Bảng `villas`:**
   - `id`: `BIGINT AUTO_INCREMENT PRIMARY KEY`
   - `villa_number`: `VARCHAR(30) NOT NULL UNIQUE` (Mã Villa: ví dụ `Villa #101`)
   - `floor`: `INT` (Số tầng / kết cấu chính, ví dụ: 1, 2, 3)
   - `structure_type`: `VARCHAR(50)` (Kết cấu kiến trúc: `1 Tầng`, `2 Tầng`, `Duplex`, `Triplex`, `Penthouse`)
   - `zone`: `VARCHAR(50)` (Phân khu: `Ngọc Trai`, `San Hô`...)
   - `base_price`: `DECIMAL(12,2)` (Đơn giá thuê 1 đêm)
   - `status`: `VARCHAR(20)` (`AVAILABLE`, `OCCUPIED`, `CLEANING`, `MAINTENANCE`)
   - `ozone_status`: `VARCHAR(30)` (`STERILIZED`, `RUNNING`, `EXPIRED`)
   - `amenities`: `TEXT` (Lưu danh sách tiện ích dịch vụ mở rộng JSON/chuỗi phân tách dấu phẩy)
   - `image_url`: `TEXT`
   - `bedroom_count`: `INT` (Số lượng phòng ngủ tự động tính)
   - `villa_type_id`: `BIGINT` (Trỏ tới Hạng Villa: `Villa N Phòng Ngủ`)
2. **Bảng `rooms` (Phòng ngủ con trong Villa):**
   - `id`: `BIGINT AUTO_INCREMENT PRIMARY KEY`
   - `villa_id`: `BIGINT NOT NULL` (Khóa ngoại trỏ về `villas.id`)
   - `room_number`: `VARCHAR(30) NOT NULL` (Ví dụ: `Villa #101-P1`, `MB-01`)
   - `name`: `VARCHAR(100)` (Ví dụ: `Phòng Ngủ Master`, `Phòng Ngủ Phụ 1`)
   - `floor`: `INT` (Vị trí tầng của phòng con)
   - `room_type_id`: `BIGINT` (Trỏ tới loại giường: `room_types.id`)
   - `status`: `VARCHAR(20)` (`AVAILABLE`)

### 3.2. DTO & Backend API
- **`VillaRequest.java`:**
  - Bổ sung `structureType`: String
  - Bổ sung `amenities`: List<String>
  - Bổ sung `bedroomCount`: Integer
  - Bổ sung `bedSelections`: List<VillaBedSelectionDto> (chế độ tích chọn nhanh: `{ roomTypeId: Long, quantity: Integer }`)
  - Bổ sung `childRooms`: List<ChildRoomRequestDto> (chế độ phân bổ phòng con chi tiết)
- **`VillaResponse.java`:**
  - Trả về đầy đủ `structureType`, `amenities`, `bedroomCount`, `rooms`, `totalAdults`, `totalChildren`, `totalCapacity`.

---

## 4. Kế Hoạch Kiểm Thử & Tiêu Chí Nghiệm Thu (Acceptance Criteria)

1. **Khởi tạo Villa với nhiều loại giường:**
   - Tích chọn 1 giường King + 1 giường Queen + 2 giường Single ➔ Thanh tóm tắt hiển thị chuẩn: `3 Phòng Ngủ • 4 Chiếc Giường • 5 Người Lớn • 1 Trẻ Nhỏ • Tối Đa 6 Khách`.
2. **Tự động xác định Hạng Villa:**
   - Biệt thự tự động được đặt hạng `Villa 3 Phòng Ngủ`.
3. **Bảo toàn dữ liệu:**
   - Phân khu `Ngọc Trai` được lưu và hiển thị chuẩn xác.
   - Trạng thái vận hành (`AVAILABLE`) được lưu đúng.
   - Hình ảnh tải lên hoặc chọn mẫu resort được hiển thị ngay lập tức không bị lỗi ảnh vỡ.
4. **Tiện ích mở rộng:**
   - Các tiện ích được tích chọn (Ozon, Quản gia, Hồ bơi...) được lưu trữ và hiển thị đầy đủ trên chi tiết biệt thự.
