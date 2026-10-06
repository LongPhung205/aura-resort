# DANH SÁCH TOÀN BỘ API ENDPOINTS - AURA HOTEL & LUXURY RESORT
> Base URL: http://localhost:8080/api/v1
> Ngày xuất: 05/10/2026

================================================================================
1. AUTHENTICATION & TÀI KHOẢN (AuthController, UserController)
================================================================================
POST   /api/v1/auth/login                  # Đăng nhập (trả về JWT token)
POST   /api/v1/auth/register               # Đăng ký tài khoản khách hàng mới
POST   /api/v1/auth/google                 # Đăng nhập nhanh Google OAuth2
POST   /api/v1/auth/forgot-password        # Gửi mã OTP khôi phục mật khẩu qua Email
POST   /api/v1/auth/verify-otp             # Xác thực mã OTP 6 số
POST   /api/v1/auth/reset-password         # Đặt lại mật khẩu mới
GET    /api/v1/users/me                    # Lấy thông tin cá nhân tài khoản đang đăng nhập
PUT    /api/v1/users/me                    # Cập nhật họ tên, sđt, avatar
PUT    /api/v1/users/me/change-password    # Đổi mật khẩu tài khoản
GET    /api/v1/users/profile               # Lấy thống kê tích điểm, hạng thẻ khách

================================================================================
2. VILLA, PHÒNG & PHÂN KHU PUBLIC (PublicVillaController, VillaController, ZoneController)
================================================================================
GET    /api/v1/public/villas               # Tìm kiếm biệt thự theo ngày check-in/out, số khách
GET    /api/v1/public/villas/{id}          # Xem chi tiết biệt thự, thư viện ảnh, tiện nghi
GET    /api/v1/public/zones                # Danh sách 3 phân khu (Ngọc Trai, Sao Biển, San Hô)
GET    /api/v1/zones                       # Danh sách phân khu
GET    /api/v1/zones/{id}                  # Chi tiết phân khu
GET    /api/v1/villa-types                 # Danh sách các hạng biệt thự & giá niêm yết
GET    /api/v1/villa-types/{id}            # Chi tiết hạng biệt thự
GET    /api/v1/room-types                  # Danh sách loại phòng
GET    /api/v1/room-types/{id}             # Chi tiết loại phòng
GET    /api/v1/villas                      # Danh sách villa
GET    /api/v1/villas/{id}                 # Chi tiết villa theo ID
GET    /api/v1/rooms                       # Danh sách phòng
GET    /api/v1/rooms/{id}                  # Chi tiết phòng theo ID

================================================================================
3. ĐẶT PHÒNG & THANH TOÁN CLIENT (BookingController, PaymentController)
================================================================================
POST   /api/v1/bookings                    # Tạo đơn đặt phòng mới (khóa phòng, tính tổng tiền)
GET    /api/v1/bookings/my-bookings        # Lịch sử đơn đặt của khách đang đăng nhập
GET    /api/v1/bookings/{id}               # Chi tiết đơn đặt phòng
POST   /api/v1/bookings/{id}/cancel        # Yêu cầu hủy đơn đặt phòng
POST   /api/v1/payments/create-momo        # Tạo link thanh toán MoMo QR / Thẻ
POST   /api/v1/payments/momo-ipn           # Webhook IPN nhận kết quả tự động từ MoMo

================================================================================
4. AI CONCIERGE & ĐÁNH GIÁ (AiChatController, ReviewController)
================================================================================
POST   /api/v1/ai/chat                     # Trợ lý ảo AI tư vấn chọn phòng & dịch vụ nghỉ dưỡng
GET    /api/v1/reviews/villa/{villaId}     # Danh sách đánh giá của căn villa
POST   /api/v1/reviews                     # Khách gửi đánh giá 5 sao sau khi trả phòng
GET    /api/v1/reviews/my-reviews          # Lịch sử đánh giá của khách

================================================================================
5. PROMOTION, BANNER, BỘ SƯU TẬP, COMBO (Public)
================================================================================
GET    /api/v1/promotions                  # Danh sách mã giảm giá
GET    /api/v1/promotions/active           # Danh sách mã giảm giá đang kích hoạt
GET    /api/v1/public/banners              # Danh sách banner trang chủ
GET    /api/v1/banners                     # Danh sách banner
GET    /api/v1/collections                 # Danh sách bộ sưu tập biệt thự
GET    /api/v1/collections/{id}            # Chi tiết bộ sưu tập
GET    /api/v1/combo-packages              # Danh sách gói Combo nghỉ dưỡng trọn gói
GET    /api/v1/combo-packages/{id}         # Chi tiết combo package
GET    /api/v1/extra-services              # Danh sách dịch vụ gia tăng (Du thuyền, Maybach, Spa)

================================================================================
6. NOTIFICATION REALTIME (NotificationController)
================================================================================
GET    /api/v1/notifications/stream        # Kênh SSE (Server-Sent Events) nhận thông báo thời gian thực

================================================================================
7. MOBILE PORTAL BUỒNG PHÒNG (HousekeepingMobileController)
================================================================================
GET    /api/v1/housekeeping/my-tasks       # Danh sách phòng được phân công cho nhân viên ca trực
POST   /api/v1/housekeeping/tasks/{id}/start       # Bắt đầu dọn phòng (chuyển IN_PROGRESS)
POST   /api/v1/housekeeping/tasks/{id}/progress    # Cập nhật tiến độ checklist dọn dẹp
POST   /api/v1/housekeeping/tasks/{id}/consumptions# Báo cáo tiêu thụ minibar / đồ uống
POST   /api/v1/housekeeping/tasks/{id}/complete    # Hoàn thành dọn phòng (chờ QC nghiệm thu)
GET    /api/v1/housekeeping/minibar-catalog        # Danh mục giá đồ uống minibar
POST   /api/v1/housekeeping/lost-found             # Báo cáo đồ đạc khách bỏ quên (Lost & Found)
POST   /api/v1/housekeeping/maintenance            # Báo cáo sự cố cần bảo trì kỹ thuật

================================================================================
8. ADMIN DASHBOARD & CORE STATS (AdminDashboardController)
================================================================================
GET    /api/v1/admin/dashboard             # Toàn bộ số liệu KPI, công suất, dòng tiền, khách VIP

================================================================================
9. ADMIN QUẢN LÝ ĐẶT PHÒNG & ĐIỀU PHỐI (AdminBookingController)
================================================================================
GET    /api/v1/admin/bookings              # Danh sách đơn đặt phân trang, lọc đa tiêu chí
GET    /api/v1/admin/bookings/gantt        # Dữ liệu Biểu đồ Gantt Timeline 16 Villa (7 ngày)
POST   /api/v1/admin/bookings/direct       # Tạo đơn đặt phòng trực tiếp tại quầy lễ tân
POST   /api/v1/admin/bookings/{id}/check-in# Thực hiện thủ tục nhận phòng (Check-in)
POST   /api/v1/admin/bookings/{id}/check-out # Thực hiện thủ tục trả phòng (Check-out)
PUT    /api/v1/admin/bookings/{id}/assign-villa # Đổi căn biệt thự được chỉ định

================================================================================
10. ADMIN QUẢN TRỊ BUỒNG PHÒNG & QC (AdminHousekeepingController)
================================================================================
GET    /api/v1/admin/housekeeping/tasks    # Toàn bộ danh sách nhiệm vụ dọn phòng toàn resort
POST   /api/v1/admin/housekeeping/tasks/{id}/assign  # Phân công buồng phòng cho nhân viên
POST   /api/v1/admin/housekeeping/tasks/{id}/inspect # Quản lý nghiệm thu QC (Đạt / Yêu cầu dọn lại)
GET    /api/v1/admin/housekeeping/inspections       # Lịch sử biên bản kiểm tra chất lượng
GET    /api/v1/admin/housekeeping/housekeepers      # Danh sách nhân viên buồng phòng đang trực
POST   /api/v1/admin/housekeeping/generate-daily    # Tự động quét sinh task dọn phòng trong ngày

================================================================================
11. ADMIN KHO VẬT TƯ & BỔ SUNG MINIBAR (AdminInventoryController, AdminRefillController)
================================================================================
GET    /api/v1/admin/inventory/items       # Danh sách kho vật tư (đồ uống, linen, amenities)
POST   /api/v1/admin/inventory/items       # Thêm mới vật tư vào kho
PUT    /api/v1/admin/inventory/items/{id}  # Sửa thông tin định mức vật tư
POST   /api/v1/admin/inventory/transactions# Nhập / Xuất kho vật tư
GET    /api/v1/admin/inventory/summary     # Thống kê tổng giá trị tồn kho & cảnh báo thiếu hàng
GET    /api/v1/admin/inventory/refill/tasks         # Danh sách lệnh bổ sung minibar cho villa
POST   /api/v1/admin/inventory/refill/tasks         # Tạo lệnh bổ sung minibar mới
PUT    /api/v1/admin/inventory/refill/tasks/{id}/items # Cập nhật danh sách đồ cần bù
POST   /api/v1/admin/inventory/refill/tasks/{id}/complete # Xác nhận đã bổ sung xong

================================================================================
12. ADMIN KẾ TOÁN, SỔ CÁI & MINIBAR (AdminBillingConsumptionController, AdminLedgerController)
================================================================================
GET    /api/v1/admin/billing/consumptions/booking/{bookingId} # Xem chi phí minibar của đơn đặt
POST   /api/v1/admin/billing/consumptions                    # Ghi nhận phát sinh tiêu thụ/hỏng đồ
POST   /api/v1/admin/billing/consumptions/{id}/reconcile     # Đối soát thanh toán minibar
GET    /api/v1/admin/payments/ledger       # Sổ cái thu chi kế toán kép
POST   /api/v1/admin/payments/ledger       # Ghi bút toán sổ cái thu / chi thủ công
GET    /api/v1/admin/payments/dashboard-stats # Thống kê dòng tiền theo cổng thanh toán
POST   /api/v1/admin/payments/day-end-closing # Khóa sổ đối soát cuối ngày (Night Audit)

================================================================================
13. ADMIN NHÂN SỰ & CA TRỰC (AdminStaffController)
================================================================================
GET    /api/v1/admin/staff/roster          # Bảng phân ca trực 24/7 (Sáng, Chiều, Đêm)
GET    /api/v1/admin/staff/schedules       # Lịch làm việc tuần/tháng của nhân sự
POST   /api/v1/admin/staff/schedules       # Xếp lịch ca trực cho nhân viên / Butler
GET    /api/v1/admin/staff/swap-requests   # Yêu cầu đổi ca của nhân viên
POST   /api/v1/admin/staff/swap-requests/{id}/approve # Quản lý duyệt đổi ca trực

================================================================================
14. ADMIN CSAT, NPS & ĐIỀU PHỐI DỊCH VỤ (AdminReviewRecoveryController, AdminServiceDispatchController)
================================================================================
GET    /api/v1/admin/reviews               # Quản lý tất cả đánh giá của khách
GET    /api/v1/admin/reviews/analytics     # Phân tích chỉ số CSAT, NPS, tỷ lệ hài lòng
POST   /api/v1/admin/reviews/recovery-tickets        # Mở ticket cứu vãn khi khách khiếu nại
PUT    /api/v1/admin/reviews/recovery-tickets/{id}   # Cập nhật kết quả giải quyết khiếu nại (<3 phút)
GET    /api/v1/admin/services/dispatches   # Danh sách lệnh điều phối Maybach, Du thuyền, Buggy
POST   /api/v1/admin/services/dispatches   # Tạo lệnh điều phối xe / du thuyền đón khách
PUT    /api/v1/admin/services/dispatches/{id}/status # Cập nhật trạng thái điều phối (Đang đón, Hoàn thành)

================================================================================
15. ADMIN GIÁ ĐỘNG, USER, PHÂN KHU & VILLA SERVICES (AdminYield, AdminUser, AdminZone, VillaService)
================================================================================
GET    /api/v1/admin/yield/rules           # Chính sách giá động mùa cao điểm / cuối tuần
POST   /api/v1/admin/yield/rules           # Thêm quy tắc giá động
PUT    /api/v1/admin/yield/rules/{id}      # Sửa quy tắc giá động
DELETE /api/v1/admin/yield/rules/{id}      # Xóa quy tắc giá động
GET    /api/v1/admin/yield/matrix          # Ma trận dự báo doanh thu tối ưu (Yield Matrix)
GET    /api/v1/admin/users                 # Quản lý người dùng, tìm kiếm & phân trang
POST   /api/v1/admin/users                 # Tạo tài khoản nhân sự / quản trị mới
PUT    /api/v1/admin/users/{id}            # Cập nhật thông tin / vai trò (Role)
PUT    /api/v1/admin/users/{id}/reset-password # Quản trị viên reset mật khẩu người dùng
DELETE /api/v1/admin/users/{id}            # Khóa / Xóa tài khoản
GET    /api/v1/admin/zones                 # Quản lý phân khu nghỉ dưỡng
POST   /api/v1/admin/zones                 # Thêm phân khu mới
PUT    /api/v1/admin/zones/{id}            # Sửa phân khu
DELETE /api/v1/admin/zones/{id}            # Xóa phân khu
GET    /api/v1/admin/villa-services        # Quản lý danh mục dịch vụ độc bản của villa
POST   /api/v1/admin/villa-services        # Thêm dịch vụ villa
PUT    /api/v1/admin/villa-services/{id}   # Sửa dịch vụ villa
DELETE /api/v1/admin/villa-services/{id}   # Xóa dịch vụ villa
GET    /api/v1/admin/banners               # Quản lý banner quảng cáo
POST   /api/v1/admin/banners               # Thêm banner mới
PUT    /api/v1/admin/banners/{id}          # Sửa banner
DELETE /api/v1/admin/banners/{id}          # Xóa banner
