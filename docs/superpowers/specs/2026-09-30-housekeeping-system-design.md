# Thiết Kế Hệ Thống Buồng Phòng (Housekeeping Ecosystem) - Aura Resort

- **Mã tài liệu**: SPEC-HK-20260930
- **Ngày lập**: 30/09/2026
- **Trạng thái**: Đã phê duyệt (Approved)
- **Tác giả / Nhóm thiết kế**: Antigravity & Aura Engineering Team

---

## 1. Bối cảnh & Mục tiêu Nghiệp vụ (Business Context & Objectives)

Tại chuỗi resort cao cấp Aura Resort (gồm Villa & Homestay), bộ phận **Buồng Phòng (Housekeeping)** đóng vai trò sống còn trong việc đảm bảo chất lượng phòng ngủ, trải nghiệm khách lưu trú và kiểm soát thất thoát tài sản / minibar.

### Mục tiêu cốt lõi:
1. **Chuẩn hóa quy trình dọn phòng theo 3 chế độ**: Checkout Deep Clean (Dọn sâu khi trả phòng), Daily Stayover (Dọn phòng hàng ngày) và Turndown Service (Chỉnh trang ban tối).
2. **Kiểm soát thất thoát & Chốt kiểm soát kép (Two-Step Verification)**:
   - Phân biệt rõ rệt 3 nhóm đồ: Minibar tính phí, Amenities tiêu hao miễn phí, Đồ vải & Tài sản phòng.
   - Nhân viên buồng phòng là người ghi nhận thực địa $\rightarrow$ Lễ tân kiểm tra và chốt tính tiền vào Folio khi khách trả phòng $\rightarrow$ Kho thủ kho duyệt xuất bù (Refill).
3. **Tối ưu trải nghiệm nhân viên hiện trường (Mobile-first)**: Giao diện web/PWA chuyên biệt cho điện thoại/tablet, thao tác 1 tay, nút bấm to, chống mất dữ liệu khi mất mạng tạm thời.
4. **Bàn điều phối thông minh cho Quản lý / Giám sát (Supervisor)**: Sơ đồ ma trận phòng trực quan theo Phân khu (Zone), phân công ca trực linh hoạt, màn hình duyệt nghiệm thu chất lượng phòng (QC Approval), quản lý sổ đồ thất lạc (Lost & Found) và phiếu báo hỏng kỹ thuật (Maintenance).

---

## 2. Kiến trúc Hệ sinh thái & Luồng Dữ liệu Liên thông (Ecosystem Architecture)

```
                     ┌──────────────────────────────────────────────┐
                     │          HOUSEKEEPING MOBILE PORTAL          │
                     │                 (/housekeeping)              │
                     └───────────────────────┬──────────────────────┘
                                             │
                       Thao tác kiểm kê & dọn│phòng thực địa
                                             ▼
                     ┌──────────────────────────────────────────────┐
                     │            SPRING BOOT BACKEND CORE          │
                     │  - HousekeepingTaskService                   │
                     │  - RoomConsumptionService                   │
                     │  - AdminRefillService                        │
                     └───────┬──────────────┬──────────────┬────────┘
                             │              │              │
      Tiêu thụ Minibar       │              │Phiếu Refill  │Báo hỏng
      & Mất/Hỏng tài sản     │              │vật tư bù     │thiết bị
                             ▼              ▼              ▼
                   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
                   │   LỄ TÂN     │ │   KHO TỔNG   │ │   KỸ THUẬT   │
                   │ (Front Desk) │ │ (Warehouse)  │ │(Maintenance) │
                   │  Chốt Folio  │ │  Xuất kho    │ │   Sửa chữa   │
                   └──────────────┘ └──────────────┘ └──────────────┘
```

### Vòng đời Trạng thái Phòng & Nhiệm vụ Buồng phòng:
1. `OCCUPIED`: Khách đang lưu trú.
2. `DIRTY`: Khách checkout $\rightarrow$ Tự động sinh `HousekeepingTask` loại `CHECKOUT_DEEP`, độ ưu tiên `RUSH` nếu phòng có khách check-in mới cùng ngày.
3. `CLEANING`: Nhân viên bấm "Bắt đầu làm phòng" trên mobile.
4. `OZONE_OPTIONAL`: Tùy chọn bật khử khuẩn/khử mùi Ozone (không bắt buộc với phòng thông thường).
5. `WAITING_QC`: Nhân viên nộp biên bản dọn dẹp và kiểm kê $\rightarrow$ Chờ Giám sát viên nghiệm thu.
6. `CLEAN_READY`: Giám sát duyệt "Đạt" $\rightarrow$ Sơ đồ Lễ tân mở khóa phòng sẵn sàng bàn giao chìa khóa cho khách.
7. `RE_CLEAN`: Giám sát từ chối do chưa đạt tiêu chuẩn $\rightarrow$ Nhân viên dọn lại các điểm ghi chú.
8. `MAINTENANCE`: Phòng có sự cố kỹ thuật nghiêm trọng cần tạm ngưng phục vụ để sửa chữa.

---

## 3. Quy chuẩn Kiểm kê & Bù đồ (Inventory, Amenities & Asset Policy)

### Phân nhóm 3 Loại đồ dùng trong phòng:

| Nhóm đồ | Ví dụ cụ thể | Quy tắc kiểm đếm & Xử lý chi phí | Luồng phê duyệt |
| :--- | :--- | :--- | :--- |
| **1. Minibar có tính phí** | Bia Heineken, Nước ngọt, Nước suối có gas Perrier, Snack hạt điều | Đếm số lượng thực tế vs Định mức bàn giao ban đầu. Chênh lệch là số lượng khách đã sử dụng. | Buồng phòng ghi nhận $\rightarrow$ Lễ tân đối chiếu và chốt vào Folio hóa đơn Checkout $\rightarrow$ Kho xuất bù số lượng tương ứng. |
| **2. Amenities tiêu hao miễn phí** | Bàn chải, kem đánh răng, lược, xà phòng tắm, dầu gội, nước suối chai complimentary, trà/cà phê gói | Tiêu chuẩn phòng miễn phí của resort. Khi checkout: dọn bỏ đồ đã mở, setup mới 100% đón khách mới. Tuyệt đối không tính tiền khách. | Nhân viên xác nhận 1 chạm: "Đã fill đủ 100%". Nếu xe buồng phòng thiếu món nào, tạo phiếu xin Kho cấp bù. |
| **3. Đồ vải & Tài sản phòng** | Khăn tắm lớn/nhỏ, áo choàng tắm, ga trải giường, vỏ gối, ấm đun nước, máy sấy tóc, ly thủy tinh | **Dơ/Hao mòn thông thường**: Gom đi giặt thường (chi phí vận hành resort, không tính tiền khách).<br>**Mất hoặc Hỏng nặng** (khách mang về, xé rách, dính mực/vết bẩn vĩnh viễn, vỡ nát): Buồng phòng bấm "Báo Mất/Hỏng" kèm ảnh chụp. | Tra cứu **Bảng giá đền bù tài sản** $\rightarrow$ Gửi sang Lễ tân để charge vào hóa đơn Checkout $\rightarrow$ Tạo phiếu Kho xuất đồ mới bù vào phòng. |

---

## 4. Thiết kế Phân hệ Mobile Tác nghiệp Thực địa (`/housekeeping`)

- **Đối tượng sử dụng**: Nhân viên dọn phòng (`ROLE_HOUSEKEEPING`) đăng nhập trên điện thoại/máy tính bảng.
- **Màn hình chính (Mobile Shift Dashboard)**:
  - Thông tin ca trực: Số phòng được giao, số phòng đã xong, tiến độ ca.
  - Tab 1: "Phòng của tôi" (Phòng đã được Supervisor chỉ định).
  - Tab 2: "Phòng trống cần dọn" (Danh sách phòng bẩn tự do trong phân khu để nhân viên bấm [Nhận phòng dọn thêm]).
  - Huy hiệu trạng thái phòng trực quan với màu sắc nhận diện (`DIRTY` - Đỏ, `CLEANING` - Vàng cam, `WAITING_QC` - Xanh dương). Cờ `RUSH` nổi bật cho phòng khách sắp check-in.
- **Màn hình Thao tác Dọn phòng (Workspace)**:
  - **Tab Checklist dọn dẹp**: 16 tiêu chuẩn phòng sao (Dọn rác $\rightarrow$ Tháo đồ vải bẩn $\rightarrow$ Cọ rửa nhà tắm $\rightarrow$ Thay ga gối make bed $\rightarrow$ Bổ sung amenities $\rightarrow$ Lau bụi gương kính $\rightarrow$ Hút bụi lau sàn).
  - **Nút tùy chọn phụ Ozone**: Chỉ kích hoạt khi phòng có mùi khói thuốc hoặc thức ăn nặng mùi; có đồng hồ đếm ngược 15-30 phút hiển thị cảnh báo tím.
  - **Tab Kiểm kê Minibar & Vật tư**:
    + Bảng minibar: Nút `+` `-` chỉnh số lượng thực tế còn lại $\rightarrow$ Tự tính số lượng khách dùng và tổng tiền tạm tính.
    + Amenities: Nút gạt "Đã setup đủ mới 100%".
    + Nút đỏ [BÁO MẤT / HỎNG ĐỒ]: Chọn món đồ, chọn tình trạng (Mất / Hỏng vĩnh viễn), chụp ảnh hiện trường, tự áp giá đền bù.
  - **Tab Tác nghiệp Bổ trợ**:
    + Nút [Báo Sự Cố Kỹ Thuật]: Chọn hạng mục (Điện, Nước, Điều hòa, Khóa cửa...), chụp ảnh hư hại, gửi ticket sửa chữa.
    + Nút [Ghi Nhận Đồ Thất Lạc]: Chụp ảnh vật phẩm khách bỏ quên, nhập vị trí nhặt được $\rightarrow$ Tự động đồng bộ sang Lễ tân.
  - **Nút [Hoàn Tất & Gửi Nghiệm Thu]**: Lưu toàn bộ dữ liệu, đẩy biên bản sang Supervisor và Lễ tân/Kho.

---

## 5. Thiết kế Phân hệ Web Admin Điều phối & Nghiệm thu (`/admin/housekeeping`)

- **Đối tượng sử dụng**: Tổ trưởng / Giám sát buồng phòng (Supervisor), Trưởng bộ phận Phòng, Quản trị viên (`ROLE_ADMIN`).
- **Sơ đồ Ma trận Phòng Thời gian thực (Live Room Matrix)**:
  - Hiển thị toàn bộ phòng theo Phân khu (Zone) và tầng.
  - Lọc nhanh: Tất cả, Phòng bẩn (`DIRTY`), Đang dọn (`CLEANING`), Chờ nghiệm thu (`WAITING_QC`), Phòng sạch (`CLEAN_READY`), Bảo trì (`MAINTENANCE`).
  - Gắn cờ ưu tiên đón khách trong ngày.
- **Bàn Phân công Ca trực (Task Dispatcher)**:
  - Danh sách nhân viên buồng phòng đang trực ca.
  - Phân công phòng 1-click hoặc kéo thả phòng cho nhân viên.
- **Quy trình Nghiệm thu Chất lượng (QC Modal)**:
  - Supervisor mở phòng `WAITING_QC`: Kiểm tra checklist, xem ảnh hiện trường, kiểm tra báo cáo minibar/đồ hỏng.
  - **Nút [Duyệt Đạt (Pass)]**: Chuyển phòng sang `CLEAN_READY` $\rightarrow$ Mở khóa cho Lễ tân giao chìa khóa cho khách.
  - **Nút [Yêu Cầu Làm Lại (Re-clean)]**: Nhập lý do (gương bẩn, sàn chưa lau kỹ...) $\rightarrow$ Bắn thông báo về điện thoại nhân viên để sửa lại điểm chưa đạt.
- **Sổ Quản lý Đồ Thất Lạc (Lost & Found Management)**:
  - Bảng tra cứu toàn bộ đồ thất lạc theo ngày, số phòng, người tìm thấy, trạng thái lưu kho / đã trả khách.
- **Giám sát Phiếu Xuất Bù Kho (Refill Pipeline Tracker)**:
  - Theo dõi tiến độ cấp phát vật tư bù từ Kho tổng về từng Villa.

---

## 6. Mô hình Dữ liệu (Database Schema / Entities)

### 6.1. Entity `HousekeepingTask` (Bổ sung/cập nhật)
```sql
ALTER TABLE housekeeping_tasks
  ADD COLUMN priority VARCHAR(20) DEFAULT 'NORMAL', -- NORMAL, RUSH
  ADD COLUMN ozone_enabled BOOLEAN DEFAULT FALSE,
  ADD COLUMN booking_id BIGINT NULL,
  ADD COLUMN re_clean_reason VARCHAR(500) NULL,
  ADD CONSTRAINT fk_hk_booking FOREIGN KEY (booking_id) REFERENCES bookings(id);
```

### 6.2. Entity `RoomConsumptionRecord` (Tạo mới)
```sql
CREATE TABLE room_consumption_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  booking_id BIGINT NULL,
  housekeeping_task_id BIGINT NOT NULL,
  villa_id BIGINT NOT NULL,
  room_id BIGINT NULL,
  item_type VARCHAR(30) NOT NULL, -- MINIBAR_CONSUMED, ASSET_DAMAGED, ASSET_LOST
  item_name VARCHAR(150) NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  unit_price DECIMAL(12,2) NOT NULL,
  total_price DECIMAL(12,2) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING_RECEPTION_APPROVAL', -- PENDING_RECEPTION_APPROVAL, APPROVED_CHARGED, WAIVED
  evidence_photo_url VARCHAR(500) NULL,
  note TEXT NULL,
  recorded_by VARCHAR(100) NOT NULL,
  approved_by VARCHAR(100) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_consumption_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
  CONSTRAINT fk_consumption_task FOREIGN KEY (housekeeping_task_id) REFERENCES housekeeping_tasks(id)
);
```

### 6.3. Entity `LostAndFoundItem` (Tạo mới)
```sql
CREATE TABLE lost_and_found_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  item_code VARCHAR(50) UNIQUE NOT NULL, -- LF-20260930-01
  villa_id BIGINT NOT NULL,
  room_id BIGINT NULL,
  booking_id BIGINT NULL,
  item_name VARCHAR(150) NOT NULL,
  category VARCHAR(50) NOT NULL, -- ELECTRONICS, JEWELRY, CLOTHING, DOCUMENTS, OTHER
  found_location VARCHAR(200) NOT NULL,
  photo_url VARCHAR(500) NULL,
  finder_name VARCHAR(100) NOT NULL,
  guest_name VARCHAR(100) NULL,
  guest_phone VARCHAR(20) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'STORED', -- STORED, GUEST_NOTIFIED, RETURNED, DISPOSED
  storage_location VARCHAR(100) DEFAULT 'Kho Buồng Phòng',
  returned_at TIMESTAMP NULL,
  note TEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 6.4. Entity `MaintenanceTicket` (Tạo mới)
```sql
CREATE TABLE maintenance_tickets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  ticket_code VARCHAR(50) UNIQUE NOT NULL, -- MT-20260930-01
  villa_id BIGINT NOT NULL,
  room_id BIGINT NULL,
  category VARCHAR(50) NOT NULL, -- AIR_CONDITIONER, PLUMBING, ELECTRICAL, DOOR_LOCK, FURNITURE, OTHER
  priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, EMERGENCY
  description TEXT NOT NULL,
  photo_url VARCHAR(500) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'REPORTED', -- REPORTED, IN_PROGRESS, RESOLVED, CANCELLED
  reported_by VARCHAR(100) NOT NULL,
  technician_name VARCHAR(100) NULL,
  resolved_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 7. Danh mục REST APIs

### 7.1. Housekeeping Mobile APIs (`/api/v1/housekeeping`)
- `GET /my-tasks`: Lấy danh sách nhiệm vụ của nhân viên đăng nhập.
- `GET /available-dirty-rooms`: Lấy danh sách phòng bẩn có thể nhận thêm.
- `POST /tasks/{id}/claim`: Nhân viên nhận dọn phòng trống.
- `POST /tasks/{id}/start`: Bắt đầu tiến trình dọn dẹp.
- `POST /tasks/{id}/progress`: Lưu tiến độ checklist từng bước.
- `POST /tasks/{id}/toggle-ozone`: Bật/tắt tùy chọn Ozone.
- `POST /tasks/{id}/submit-inspection`: Nộp kết quả kiểm kê (Minibar tiêu thụ, xác nhận Amenities, báo Mất/Hỏng).
- `POST /tasks/{id}/submit-qc`: Hoàn tất dọn phòng, gửi nghiệm thu.
- `POST /lost-found`: Báo cáo đồ khách bỏ quên.
- `POST /maintenance-tickets`: Báo cáo sự cố kỹ thuật phòng.

### 7.2. Supervisor Web Admin APIs (`/api/v1/admin/housekeeping`)
- `GET /matrix`: Lấy sơ đồ toàn bộ phòng resort kèm trạng thái dọn dẹp và cờ ưu tiên.
- `POST /tasks/assign`: Phân công phòng cho nhân viên.
- `POST /tasks/{id}/approve`: Nghiệm thu Đạt $\rightarrow$ chuyển trạng thái `CLEAN_READY`.
- `POST /tasks/{id}/reject`: Từ chối nghiệm thu, yêu cầu dọn lại kèm lý do $\rightarrow$ `RE_CLEAN`.
- `GET /lost-found`: Tra cứu và cập nhật trạng thái sổ đồ thất lạc.
- `GET /maintenance-tickets`: Theo dõi danh sách ticket sự cố phòng.

### 7.3. Front Desk / Checkout Billing APIs (`/api/v1/admin/billing`)
- `GET /pending-consumptions?bookingId={id}`: Lấy danh sách đồ Minibar đã dùng & Mất/Hỏng chờ duyệt của đơn đặt phòng.
- `POST /consumptions/{id}/approve`: Lễ tân chốt duyệt tính tiền vào Folio.
- `POST /consumptions/{id}/waive`: Lễ tân miễn giảm khoản phí.

---

## 8. Xử lý Tình huống Biên (Edge Cases)
1. **Khách Express Checkout vội**: Lễ tân kiểm tra nhanh trạng thái phòng; cho phép khai báo nhanh, hệ thống đối soát tự động khi buồng phòng nộp biên bản.
2. **Khách treo biển DND / Không làm phiền**: Nút bấm nhanh [Báo DND] lưu mốc thời gian, tránh đánh giá nhân viên chậm trễ và thông báo cho Lễ tân.
3. **Phòng Ưu tiên Check-in gấp (Rush Clean)**: Cờ đỏ nhấp nháy, tự động ưu tiên đẩy lên đầu danh sách dọn dẹp trên điện thoại nhân viên.
4. **Mất sóng Wifi/4G trong Villa**: Lưu tạm tiến độ checklist và kiểm kê vào `LocalStorage`, tự động đồng bộ khi có mạng trở lại.
5. **Khách khiếu nại làm hỏng đồ**: Bắt buộc có ảnh chụp chứng cứ tại hiện trường đính kèm biên bản kiểm kê để giải quyết minh bạch.

---

## 9. Kế hoạch Kiểm thử (Verification & Testing)
1. **Unit Test**:
   - Kiểm thử thuật toán tính chênh lệch tồn thực tế vs định mức (Định mức 4, thực tế 2 $\rightarrow$ Tiêu thụ 2 $\rightarrow$ Tổng tiền = 2 × Giá niêm yết).
   - Kiểm thử máy trạng thái phòng (`DIRTY` $\rightarrow$ `CLEANING` $\rightarrow$ `WAITING_QC` $\rightarrow$ `CLEAN_READY` / `RE_CLEAN`).
2. **Integration Test**:
   - Kiểm thử chốt kiểm soát kép: Nhân viên nộp biên bản kiểm kê $\rightarrow$ Lễ tân duyệt $\rightarrow$ Cập nhật hóa đơn Booking & Tự động sinh `RefillTask` gửi Kho.
3. **E2E / Responsive Test**:
   - Kiểm thử giao diện Mobile Portal trên kích thước màn hình điện thoại (375px - 430px) cho nhân viên `hoa.housekeeping@auraholdings.vn`.
   - Kiểm thử giao diện Web Admin cho Supervisor nghiệm thu và phân công phòng.
