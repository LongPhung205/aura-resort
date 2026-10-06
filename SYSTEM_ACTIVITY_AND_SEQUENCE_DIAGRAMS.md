# TÀI LIỆU TOÀN DIỆN: SƠ ĐỒ HOẠT ĐỘNG & SƠ ĐỒ TUẦN TỰ HỆ THỐNG
## HỆ THỐNG QUẢN TRỊ KHÁCH SẠN & NGHỈ DƯỠNG CAO CẤP - AURA RESORT & LUXURY VILLAS
> **Phiên bản:** 2.0 Enterprise  
> **Cập nhật:** 05/10/2026  
> **Kiến trúc:** Angular 19 (Frontend) • Spring Boot 3.x (Backend RESTful & SSE) • MySQL 8.0 • MoMo / VNPay / OnePay • Google Gemini AI

---

## MỤC LỤC TỔNG QUAN

1. [Tổng Quan Kiến Trúc & Luồng Dữ Liệu Toàn Hệ Thống](#1-tổng-quan-kiến-trúc--luồng-dữ-liệu-toàn-hệ-thống)
2. [Phân Hệ 1: Xác Thực & Quản Lý Tài Khoản (Authentication & User Management)](#phân-hệ-1-xác-thực--quản-lý-tài-khoản)
   - 1.1 Sơ Đồ Hoạt Động (Activity Diagram): Đăng Ký, Đăng Nhập & Quên Mật Khẩu OTP
   - 1.2 Sơ Đồ Tuần Tự (Sequence Diagram): Đăng Nhập & Cấp Phát JWT Token
   - 1.3 Sơ Đồ Tuần Tự (Sequence Diagram): Khôi Phục Mật Khẩu Qua Mã OTP Email
3. [Phân Hệ 2: Khám Phá & Đặt Phòng Nghỉ Dưỡng Trực Tuyến (Guest Booking)](#phân-hệ-2-khám-phá--đặt-phòng-nghỉ-dưỡng-trực-tuyến)
   - 2.1 Sơ Đồ Hoạt Động (Activity Diagram): Tìm Kiếm, Chọn Villa, Giữ Chỗ & Đặt Phòng
   - 2.2 Sơ Đồ Tuần Tự (Sequence Diagram): Khách Tạo Đơn Đặt Phòng Trực Tuyến
4. [Phân Hệ 3: Cổng Thanh Toán MoMo / VNPay & Xử Lý Webhook IPN](#phân-hệ-3-cổng-thanh-toán-momo--vnpay--xử-lý-webhook-ipn)
   - 3.1 Sơ Đồ Hoạt Động (Activity Diagram): Thanh Toán Trực Tuyến & Đối Soát Đơn
   - 3.2 Sơ Đồ Tuần Tự (Sequence Diagram): Tạo Link Thanh Toán & Nhận Webhook IPN Realtime
5. [Phân Hệ 4: Trợ Lý Ảo AI Concierge Tư Vấn Nghỉ Dưỡng](#phân-hệ-4-trợ-lý-ảo-ai-concierge-tư-vấn-nghỉ-dưỡng)
   - 4.1 Sơ Đồ Hoạt Động (Activity Diagram): AI Hỏi Đáp & Đề Xuất Villa Thông Minh
   - 4.2 Sơ Đồ Tuần Tự (Sequence Diagram): Luồng Xử Lý Prompt & Trả Lời Gợi Ý Biệt Thự
6. [Phân Hệ 5: Nghiệp Vụ Lễ Tân - Check-in, Check-out & Đặt Trực Tiếp Tại Quầy](#phân-hệ-5-nghiệp-vụ-lễ-tân---check-in-check-out--đặt-trực-tiếp-tại-quầy)
   - 5.1 Sơ Đồ Hoạt Động (Activity Diagram): Quy Trình Tiếp Đón Check-in & Trả Phòng Check-out
   - 5.2 Sơ Đồ Tuần Tự (Sequence Diagram): Tiếp Nhận Check-in Đơn Phòng Có Sẵn
   - 5.3 Sơ Đồ Tuần Tự (Sequence Diagram): Trả Phòng (Check-out) & Kích Hoạt Dọn Dẹp
   - 5.4 Sơ Đồ Tuần Tự (Sequence Diagram): Tạo Đơn Đặt Trực Tiếp Tại Quầy (Walk-in Booking)
7. [Phân Hệ 6: Quản Trị Buồng Phòng, Quy Trình Dọn 16 Bước & Mobile Portal](#phân-hệ-6-quản-trị-buồng-phòng-quy-trình-dọn-16-bước--mobile-portal)
   - 6.1 Sơ Đồ Hoạt Động (Activity Diagram): Vòng Đời Buồng Phòng (Cleaning Lifecycle)
   - 6.2 Sơ Đồ Tuần Tự (Sequence Diagram): Nhân Viên Thao Tác Mobile Portal & Nghiệm Thu QC
8. [Phân Hệ 7: Kiểm Kê Minibar, Bổ Sung Kho & Đối Soát Hóa Đơn](#phân-hệ-7-kiểm-kê-minibar-bổ-sung-kho--đối-soát-hóa-đơn)
   - 7.1 Sơ Đồ Hoạt Động (Activity Diagram): Ghi Nhận Tiêu Thụ, Bổ Sung & Tính Hóa Đơn
   - 7.2 Sơ Đồ Tuần Tự (Sequence Diagram): Buồng Phòng Báo Tiêu Thụ -> Kế Toán Đối Soát Chốt Bill
9. [Phân Hệ 8: Điều Phối Phương Tiện VIP & Dịch Vụ Độc Bản](#phân-hệ-8-điều-phối-phương-tiện-vip--dịch-vụ-độc-bản)
   - 8.1 Sơ Đồ Hoạt Động (Activity Diagram): Đón Tiễn Maybach, Du Thuyền & Trực Thăng
   - 8.2 Sơ Đồ Tuần Tự (Sequence Diagram): Lập Lệnh Điều Phối & Bàn Giao Quản Gia Butler
10. [Phân Hệ 9: Đánh Giá CSAT, NPS & Quy Trình Cứu Vãn Khiếu Nại Dưới 3 Phút](#phân-hệ-9-đánh-giá-csat-nps--quy-trình-cứu-vãn-khiếu-nại)
    - 9.1 Sơ Đồ Hoạt Động (Activity Diagram): Thu Thập Đánh Giá & Kích Hoạt Ticket Khẩn Cấp
    - 9.2 Sơ Đồ Tuần Tự (Sequence Diagram): Gửi Đánh Giá -> Báo Động Khiếu Nại -> Xử Lý SLA < 3 Phút
11. [Phân Hệ 10: Xếp Ca Trực 24/7, Đổi Ca & Khóa Sổ Kế Toán Cuối Ngày (Night Audit)](#phân-hệ-10-xếp-ca-trực-247-đổi-ca--khóa-sổ-cuối-ngày)
    - 10.1 Sơ Đồ Hoạt Động (Activity Diagram): Xếp Lịch, Đổi Ca & Khóa Sổ Cuối Ngày
    - 10.2 Sơ Đồ Tuần Tự (Sequence Diagram): Yêu Cầu Đổi Ca & Quản Lý Phê Duyệt
    - 10.3 Sơ Đồ Tuần Tự (Sequence Diagram): Quy Trình Kiểm Toán Đêm & Khóa Sổ Đối Soát (Night Audit)

---

## 1. TỔNG QUAN KIẾN TRÚC & LUỒNG DỮ LIỆU TOÀN HỆ THỐNG

Hệ thống được thiết kế theo mô hình phân tầng hướng dịch vụ hiện đại:
- **Tầng Client / Presentation:** Khách hàng (Website SPA, Mobile responsive), Nhân viên buồng phòng (Mobile Web Portal), Bộ phận Lễ tân & Quản trị (Admin Backoffice SPA).
- **Tầng API Gateway / Controller:** Spring Boot REST Controllers (`/api/v1/*`), Spring Security JWT Filter, SSE Emitter (`/notifications/stream`).
- **Tầng Business Logic & Service:** Xử lý toàn bộ logic nghiệp vụ lưu trú, tính giá động, khóa giữ phòng, điều phối dịch vụ, kiểm toán sổ cái kép.
- **Tầng Data & Integration:** MySQL Database (JPA/Hibernate), MoMo/VNPay API, Cloudinary Storage, SMTP Mail Server, Google Gemini AI Engine.

```mermaid
flowchart TB
    subgraph CLIENT_LAYER ["Tầng Giao Diện Người Dùng (Client Layer)"]
        GuestClient["Khách Hàng (Website Booking SPA)"]
        HousekeeperApp["Nhân Viên Buồng Phòng (Mobile Web Portal)"]
        AdminPortal["Quản Trị Viên & Lễ Tân (Admin Backoffice)"]
    end

    subgraph API_GATEWAY ["Tầng API Gateway & Security"]
        SecurityFilter["Spring Security 6 (JWT Authentication Filter)"]
        CORS["CORS & Request Interceptors"]
        SSEServer["SSE Realtime Notification Stream"]
    end

    subgraph BACKEND_SERVICES ["Tầng Dịch Vụ Nghiệp Vụ (Spring Boot Services)"]
        AuthSvc["Auth & User Service"]
        BookingSvc["Booking & Pricing Engine"]
        PaymentSvc["Payment & Transaction Service"]
        HousekeepingSvc["Housekeeping & QC Service"]
        InventorySvc["Inventory & Minibar Service"]
        DispatchSvc["VIP Service Dispatcher"]
        LedgerSvc["Double-Entry Ledger & Night Audit"]
        ReviewSvc["Review & SLA Recovery Service"]
        AISvc["AI Concierge Chat Service"]
    end

    subgraph DATA_EXTERNAL ["Tầng Dữ Liệu & Đối Tác Bên Ngoài"]
        MySQL[(Cơ Sở Dữ Liệu MySQL)]
        MoMo["Cổng Thanh Toán MoMo / VNPay"]
        MailServer["Máy Chủ Gửi Email (SMTP)"]
        Cloudinary["Lưu Trữ Ảnh Cloudinary"]
        GeminiAI["Google Gemini AI API"]
    end

    GuestClient --> CORS --> SecurityFilter
    HousekeeperApp --> CORS --> SecurityFilter
    AdminPortal --> CORS --> SecurityFilter

    SecurityFilter --> AuthSvc
    SecurityFilter --> BookingSvc
    SecurityFilter --> PaymentSvc
    SecurityFilter --> HousekeepingSvc
    SecurityFilter --> InventorySvc
    SecurityFilter --> DispatchSvc
    SecurityFilter --> LedgerSvc
    SecurityFilter --> ReviewSvc
    SecurityFilter --> AISvc
    SecurityFilter --> SSEServer

    BookingSvc --> MySQL
    HousekeepingSvc --> MySQL
    PaymentSvc --> MoMo
    PaymentSvc --> MySQL
    AuthSvc --> MailServer
    HousekeepingSvc --> Cloudinary
    AISvc --> GeminiAI
    BookingSvc -. Thông Báo Đổi Trạng Thái .-> SSEServer
    SSEServer -. Realtime Push .-> AdminPortal
    SSEServer -. Realtime Push .-> HousekeeperApp
```

---

## PHÂN HỆ 1: XÁC THỰC & QUẢN LÝ TÀI KHOẢN

### 1.1 Sơ Đồ Hoạt Động (Activity Diagram): Đăng Ký, Đăng Nhập & Quên Mật Khẩu OTP

```mermaid
flowchart TD
    Start([Bắt đầu]) --> ActionChoice{Người dùng chọn hành động}

    %% Nhánh Đăng Ký
    ActionChoice -->|Đăng Ký Mới| RegForm[Nhập Họ tên, Email, SĐT, Mật khẩu]
    RegForm --> ValidateReg{Kiểm tra tính hợp lệ}
    ValidateReg -->|Không hợp lệ| RegError[Báo lỗi: Trùng Email / Sai format] --> RegForm
    ValidateReg -->|Hợp lệ| HashPass[Mã hóa mật khẩu BCrypt]
    HashPass --> CreateAcc[Lưu tài khoản vào Database]
    CreateAcc --> SendWelcomeMail[Gửi Email chào mừng thành viên]
    SendWelcomeMail --> AutoLogin[Tự động đăng nhập & Cấp JWT Token]

    %% Nhánh Đăng Nhập
    ActionChoice -->|Đăng Nhập| LoginForm[Nhập Email & Mật khẩu]
    LoginForm --> CheckCreds{Kiểm tra thông tin}
    CheckCreds -->|Sai thông tin| LoginError[Báo lỗi: Sai tài khoản hoặc mật khẩu] --> LoginForm
    CheckCreds -->|Bị khóa| LockedError[Báo lỗi: Tài khoản đã bị tạm khóa] --> End([Kết thúc])
    CheckCreds -->|Chính xác| GenJWT[Sinh JWT Access Token & Refresh Token]
    GenJWT --> RoleCheck{Phân loại vai trò}
    RoleCheck -->|ROLE_ADMIN / STAFF| AdminHome[Chuyển hướng trang Quản trị Admin]
    RoleCheck -->|ROLE_CUSTOMER| UserHome[Chuyển hướng trang Đặt phòng Khách]

    %% Nhánh Quên Mật Khẩu
    ActionChoice -->|Quên Mật Khẩu| ForgotForm[Nhập Email đã đăng ký]
    ForgotForm --> CheckEmailExist{Email có tồn tại?}
    CheckEmailExist -->|Không| EmailNotExist[Thông báo: Email không tồn tại] --> ForgotForm
    CheckEmailExist -->|Có| GenOTP[Tạo mã OTP 6 số ngẫu nhiên • Hạn 5 phút]
    GenOTP --> SaveOTP[Lưu OTP vào cache/bảng tạm]
    SaveOTP --> SendOTPEmail[Gửi Email chứa mã OTP đến người dùng]
    SendOTPEmail --> EnterOTP[Người dùng nhập mã OTP 6 số]
    EnterOTP --> VerifyOTP{Kiểm tra mã OTP}
    VerifyOTP -->|Sai hoặc Quá hạn| OTPError[Báo lỗi mã OTP không hợp lệ] --> EnterOTP
    VerifyOTP -->|Chính xác| ResetPassForm[Nhập mật khẩu mới & xác nhận]
    ResetPassForm --> UpdatePass[Cập nhật mật khẩu mới BCrypt vào Database]
    UpdatePass --> ClearOTP[Hủy mã OTP cũ]
    ClearOTP --> ResetSuccess[Thông báo thành công • Chuyển sang Đăng nhập]

    AutoLogin --> SuccessEnd([Đăng nhập thành công])
    AdminHome --> SuccessEnd
    UserHome --> SuccessEnd
    ResetSuccess --> LoginForm
```

### 1.2 Sơ Đồ Tuần Tự (Sequence Diagram): Đăng Nhập & Cấp Phát JWT Token

```mermaid
sequenceDiagram
    autonumber
    actor User as Khách Hàng / Quản Trị
    participant UI as Angular Web SPA
    participant AuthCtrl as AuthController (/api/v1/auth/login)
    participant AuthSvc as AuthServiceImpl
    participant UserRepo as UserRepository
    participant TokenProv as JwtTokenProvider
    participant DB as Cơ Sở Dữ Liệu MySQL

    User->>UI: Nhập Email và Mật khẩu, nhấn "Đăng Nhập"
    UI->>UI: Validate Client (Email format, Password required)
    UI->>AuthCtrl: POST /api/v1/auth/login (LoginRequest)
    AuthCtrl->>AuthSvc: authenticateUser(request)
    AuthSvc->>UserRepo: findByEmail(email)
    UserRepo->>DB: SELECT * FROM users WHERE email = ?
    DB-->>UserRepo: Trả về bản ghi User (nếu có)
    UserRepo-->>AuthSvc: User Entity

    alt Không tìm thấy User hoặc sai mật khẩu
        AuthSvc-->>AuthCtrl: Throw BadCredentialsException
        AuthCtrl-->>UI: 401 Unauthorized (Sai email hoặc mật khẩu)
        UI-->>User: Hiển thị thông báo đăng nhập thất bại
    else User bị vô hiệu hóa (active = false)
        AuthSvc-->>AuthCtrl: Throw DisabledException
        AuthCtrl-->>UI: 403 Forbidden (Tài khoản đang bị khóa)
        UI-->>User: Hiển thị liên hệ quản trị viên
    else Xác thực thành công
        AuthSvc->>TokenProv: generateToken(UserPrincipal)
        TokenProv-->>AuthSvc: jwtTokenString
        AuthSvc-->>AuthCtrl: AuthResponse(token, userProfile, roles)
        AuthCtrl-->>UI: 200 OK + JWT Token + User Info
        UI->>UI: Lưu JWT Token vào LocalStorage / NgRx Store
        UI->>UI: Phân quyền & Điều hướng theo Role
        UI-->>User: Hiển thị giao diện tương ứng (Admin / Client)
    end
```

### 1.3 Sơ Đồ Tuần Tự (Sequence Diagram): Khôi Phục Mật Khẩu Qua Mã OTP Email

```mermaid
sequenceDiagram
    autonumber
    actor User as Người Dùng Quên Mật Khẩu
    participant UI as Angular SPA (ForgotPassword)
    participant AuthCtrl as AuthController
    participant AuthSvc as AuthServiceImpl
    participant MailSvc as EmailServiceImpl
    participant UserRepo as UserRepository
    participant DB as MySQL Database

    User->>UI: Nhập Email yêu cầu khôi phục mật khẩu
    UI->>AuthCtrl: POST /api/v1/auth/forgot-password { email }
    AuthCtrl->>AuthSvc: processForgotPassword(email)
    AuthSvc->>UserRepo: findByEmail(email)
    UserRepo->>DB: SELECT * FROM users WHERE email = ?
    DB-->>UserRepo: User Record

    alt Email không tồn tại
        AuthSvc-->>AuthCtrl: Throw ResourceNotFoundException
        AuthCtrl-->>UI: 404 Not Found ("Email không tồn tại trong hệ thống")
        UI-->>User: Báo lỗi không tìm thấy tài khoản
    else Email hợp lệ
        AuthSvc->>AuthSvc: Tạo mã OTP 6 số + Hạn dùng (LocalDateTime.now() + 5 phút)
        AuthSvc->>DB: UPDATE users SET otp_code=?, otp_expired_at=? WHERE email=?
        AuthSvc->>MailSvc: sendOtpPasswordResetEmail(email, otpCode)
        MailSvc-->>User: Gửi email chứa mã OTP xác thực
        AuthSvc-->>AuthCtrl: Thành công
        AuthCtrl-->>UI: 200 OK ("Mã xác thực OTP đã được gửi đến email")
        UI-->>User: Mở màn hình nhập mã OTP và mật khẩu mới

        User->>UI: Nhập mã OTP 6 số + Mật khẩu mới
        UI->>AuthCtrl: POST /api/v1/auth/reset-password { email, otpCode, newPassword }
        AuthCtrl->>AuthSvc: resetPassword(request)
        AuthSvc->>UserRepo: findByEmailAndOtpCode(email, otpCode)
        UserRepo->>DB: SELECT * FROM users WHERE email=? AND otp_code=?
        DB-->>UserRepo: Kết quả so khớp

        alt OTP sai hoặc đã hết hạn
            AuthSvc-->>AuthCtrl: Throw BusinessException("Mã OTP không đúng hoặc đã hết hạn")
            AuthCtrl-->>UI: 400 Bad Request
            UI-->>User: Báo lỗi mã xác thực không chính xác
        else OTP hợp lệ
            AuthSvc->>AuthSvc: BCryptPasswordEncoder.encode(newPassword)
            AuthSvc->>DB: UPDATE users SET password=?, otp_code=NULL, otp_expired_at=NULL
            AuthSvc-->>AuthCtrl: Reset mật khẩu thành công
            AuthCtrl-->>UI: 200 OK ("Đổi mật khẩu thành công")
            UI-->>User: Thông báo thành công và chuyển sang màn hình Đăng Nhập
        end
    end
```

---

## PHÂN HỆ 2: KHÁM PHÁ & ĐẶT PHÒNG NGHỈ DƯỠNG TRỰC TUYẾN

### 2.1 Sơ Đồ Hoạt Động (Activity Diagram): Tìm Kiếm, Chọn Villa, Giữ Chỗ & Đặt Phòng

```mermaid
flowchart TD
    Start([Khách truy cập Website]) --> SearchBar[Nhập: Ngày Check-in, Ngày Check-out, Số Khách, Phân Khu]
    SearchBar --> SubmitSearch[Gửi yêu cầu tìm phòng trống]
    SubmitSearch --> QueryDB[(Truy vấn Biệt thự khả dụng theo ngày)]
    QueryDB --> CheckResult{Có phòng phù hợp?}

    CheckResult -->|Không có| EmptyList[Hiển thị gợi ý: Đổi ngày hoặc chọn phân khu lân cận] --> SearchBar
    CheckResult -->|Có phòng| ShowList[Hiển thị danh sách Biệt Thự: Ảnh, Giá, Tiện nghi, Sức chứa]

    ShowList --> SelectVilla[Khách xem chi tiết Villa & chọn căn ưng ý]
    SelectVilla --> AddServices{Chọn dịch vụ gia tăng?}
    AddServices -->|Có| PickServices[Chọn: Maybach đưa đón, Tiệc BBQ, Spa, Bữa tối ngắm hoàng hôn]
    AddServices -->|Không| GuestInfo[Chuyển đến màn hình Nhập Thông Tin Khách Hàng]
    PickServices --> GuestInfo

    GuestInfo --> FillDetails[Điền: Họ tên, Số điện thoại, Email, Ghi chú / Dị ứng]
    FillDetails --> ApplyVoucher{Áp dụng mã giảm giá?}
    ApplyVoucher -->|Có| EnterCode[Nhập mã Voucher / Khuyến mãi]
    EnterCode --> ValidateVoucher{Voucher hợp lệ?}
    ValidateVoucher -->|Không hợp lệ| VoucherErr[Báo lỗi voucher hết hạn / không đủ điều kiện] --> EnterCode
    ValidateVoucher -->|Hợp lệ| ApplyDiscount[Trừ tiền ưu đãi vào tổng hóa đơn] --> PaymentMethod
    ApplyVoucher -->|Không| PaymentMethod[Chọn phương thức thanh toán]

    PaymentMethod --> ChoosePay{Hình thức thanh toán}
    ChoosePay -->|Thanh toán tại Lễ tân| CreatePendingBooking[Tạo đơn trạng thái PENDING • Chờ xác nhận]
    ChoosePay -->|Cổng điện tử MoMo / VNPay| CreatePaymentIntent[Khóa tạm phòng • Chuyển sang Cổng Thanh Toán]

    CreatePendingBooking --> SendConfirmEmail[Gửi email xác nhận đặt chỗ] --> FinishBooking([Hoàn tất đặt phòng])
    CreatePaymentIntent --> GateRedirect[Chuyển hướng sang App/Trang MoMo/VNPay]
```

### 2.2 Sơ Đồ Tuần Tự (Sequence Diagram): Khách Tạo Đơn Đặt Phòng Trực Tuyến

```mermaid
sequenceDiagram
    autonumber
    actor Guest as Khách Hàng
    participant Web as Angular Client App
    participant BookCtrl as BookingController (/api/v1/bookings)
    participant BookSvc as BookingServiceImpl
    participant VillaRepo as VillaRepository
    participant BookRepo as BookingRepository
    participant MailSvc as EmailServiceImpl
    participant SSENotif as NotificationService
    participant DB as MySQL Database

    Guest->>Web: Nhấn "Xác Nhận Đặt Biệt Thự"
    Web->>BookCtrl: POST /api/v1/bookings (CreateBookingRequest)
    Note over Web,BookCtrl: Payload: villaId, checkIn, checkOut, guestName, phone, email, extraServices, note
    BookCtrl->>BookSvc: createBooking(request)
    
    BookSvc->>VillaRepo: checkVillaAvailability(villaId, checkIn, checkOut)
    VillaRepo->>DB: SELECT COUNT(*) FROM bookings WHERE villa_id=? AND status IN ('CONFIRMED','CHECKED_IN') AND (overlap dates)
    DB-->>VillaRepo: 0 (Phòng trống, không bị trùng lịch)
    
    alt Phòng đã bị người khác đặt trước trong khoảng ngày này
        BookSvc-->>BookCtrl: Throw BusinessException("Biệt thự đã có khách đặt trong khoảng thời gian này")
        BookCtrl-->>Web: 400 Bad Request
        Web-->>Guest: Hiển thị cảnh báo biệt thự vừa được giữ chỗ, vui lòng chọn căn khác
    else Phòng khả dụng
        BookSvc->>BookSvc: Tính số đêm = checkOut - checkIn
        BookSvc->>BookSvc: Tính tiền phòng = basePrice * nights * hệ số giá động
        BookSvc->>BookSvc: Tính phụ thu dịch vụ gia tăng (nếu có)
        BookSvc->>BookSvc: Tạo mã đơn độc bản: BK-YYYYMMDD-XXXX
        
        BookSvc->>BookRepo: save(BookingEntity: status=CONFIRMED/PENDING)
        BookRepo->>DB: INSERT INTO bookings, booking_details, booking_extra_services
        DB-->>BookRepo: Booking Saved (ID = 1052)
        
        BookSvc->>MailSvc: sendBookingConfirmationEmail(booking)
        MailSvc-->>Guest: Gửi Email xác nhận kèm mã Check-in tức thì
        
        BookSvc->>SSENotif: sendNotification("NEW_BOOKING_CREATED", bookingId)
        SSENotif-->>Web: Realtime Push đến Quầy Lễ Tân / Admin Dashboard
        
        BookSvc-->>BookCtrl: BookingResponseDTO
        BookCtrl-->>Web: 201 Created + Chi tiết đơn đặt
        Web-->>Guest: Hiển thị màn hình "Đặt Phòng Thành Công" + Mã QR Check-in
    end
```

---

## PHÂN HỆ 3: CỔNG THANH TOÁN MOMO / VNPAY & XỬ LÝ WEBHOOK IPN

### 3.1 Sơ Đồ Hoạt Động (Activity Diagram): Thanh Toán Trực Tuyến & Đối Soát Đơn

```mermaid
flowchart TD
    Start([Khách chọn thanh toán trực tuyến]) --> SelectGate{Chọn Cổng}
    SelectGate -->|MoMo| ReqMoMo[Hệ thống gọi API MoMo tạo giao dịch]
    SelectGate -->|VNPay| ReqVNPay[Hệ thống tạo URL thanh toán VNPay]

    ReqMoMo --> ReturnPayUrl[Nhận URL thanh toán & Mã giao dịch TXN]
    ReqVNPay --> ReturnPayUrl
    ReturnPayUrl --> RedirectClient[Chuyển hướng trình duyệt khách sang Cổng thanh toán]

    RedirectClient --> UserAction{Khách thao tác trên Cổng}
    UserAction -->|Hủy thanh toán / Thoát| ClientCancel[Quay về trang web với thông báo Hủy] --> UnlockRoom[Hủy giữ phòng nếu quá 15 phút] --> EndFail([Kết thúc thất bại])
    UserAction -->|Xác nhận & Trừ tiền thành công| GateProcess[Cổng xử lý giao dịch thành công]

    GateProcess --> AsyncIPN[Cổng gửi Webhook IPN chạy ngầm Server-to-Server]
    GateProcess --> SyncRedirect[Cổng chuyển hướng khách về trang Return URL]

    AsyncIPN --> VerifySig{Xác thực chữ ký HMAC SHA256}
    VerifySig -->|Sai chữ ký| RejectIPN[Từ chối cập nhật • Ghi log bảo mật] --> EndFail
    VerifySig -->|Chữ ký hợp lệ| CheckAmount{Kiểm tra số tiền thanh toán}

    CheckAmount -->|Sai lệch tiền| FlagFraud[Đánh dấu cảnh báo gian lận tài chính] --> EndFail
    CheckAmount -->|Chính xác 100%| UpdateDB[Cập nhật Booking: status=CONFIRMED, is_fully_paid=true]

    UpdateDB --> InsertLedger[Tự động ghi bút toán Sổ Cái Kế Toán]
    InsertLedger --> PushSSE[Bắn thông báo SSE tới Lễ Tân: Đã thanh toán 100%]
    PushSSE --> SendInvoiceMail[Gửi hóa đơn điện tử E-Invoice qua Email khách]

    SyncRedirect --> ShowSuccessPage[Giao diện hiển thị: Thanh toán thành công 100%]
    ShowSuccessPage --> EndSuccess([Giao dịch hoàn tất])
```

### 3.2 Sơ Đồ Tuần Tự (Sequence Diagram): Tạo Link Thanh Toán & Nhận Webhook IPN Realtime

```mermaid
sequenceDiagram
    autonumber
    actor Guest as Khách Hàng
    participant UI as Angular Web Checkout
    participant PayCtrl as PaymentController
    participant PaySvc as PaymentServiceImpl
    participant MoMoAPI as Cổng Thanh Toán MoMo Gateway
    participant BookRepo as BookingRepository
    participant LedgerSvc as AdminLedgerServiceImpl
    participant SSE as NotificationService

    Guest->>UI: Chọn thanh toán ví MoMo và bấm "Thanh Toán Ngay"
    UI->>PayCtrl: POST /api/v1/payments/create-momo { bookingId, amount }
    PayCtrl->>PaySvc: createMoMoPayment(bookingId, amount)
    PaySvc->>PaySvc: Tạo HMAC-SHA256 signature với SecretKey
    PaySvc->>MoMoAPI: POST https://test-payment.momo.vn/v2/gateway/api/create
    MoMoAPI-->>PaySvc: 200 OK { payUrl, qrCodeUrl, orderId, requestId }
    PaySvc-->>PayCtrl: PaymentLinkResponse(payUrl)
    PayCtrl-->>UI: 200 OK + payUrl
    UI-->>Guest: Chuyển hướng sang App/Web MoMo quét mã QR

    Guest->>MoMoAPI: Quét mã QR & Xác nhận thanh toán mã PIN/OTP
    MoMoAPI->>MoMoAPI: Trừ tiền ví khách & Xử lý giao dịch thành công

    par Xử lý Bất đồng bộ Server-to-Server (IPN Webhook)
        MoMoAPI->>PayCtrl: POST /api/v1/payments/momo-ipn (MoMoIPNPayload)
        PayCtrl->>PaySvc: processMoMoIPN(payload)
        PaySvc->>PaySvc: Kiểm tra chữ ký đối soát (Verify HMAC-SHA256)
        
        alt Chữ ký hợp lệ & resultCode == 0 (Thành công)
            PaySvc->>BookRepo: updatePaymentStatus(bookingId, PAID, txId)
            PaySvc->>LedgerSvc: recordEntry(Thu tiền đặt phòng qua MoMo, amount)
            PaySvc->>SSE: sendNotification("PAYMENT_SUCCESS_CONFIRMED", bookingId)
            SSE-->>UI: Cập nhật thẻ phòng sang ĐÃ THANH TOÁN trên Dashboard
            PaySvc-->>MoMoAPI: 204 No Content / 200 OK (Xác nhận đã nhận IPN)
        else Giao dịch thất bại
            PaySvc->>BookRepo: updatePaymentStatus(bookingId, FAILED)
            PaySvc-->>MoMoAPI: 200 OK
        end
    and Khách được chuyển hướng về Website
        MoMoAPI-->>UI: Redirect to /checkout/success?orderId=...
        UI->>UI: Hiển thị Thẻ Xác Nhận Đã Thu Đủ 100% Giá Trị Kỳ Nghỉ
    end
```

---

## PHÂN HỆ 4: TRỢ LÝ ẢO AI CONCIERGE TƯ VẤN NGHỈ DƯỠNG

### 4.1 Sơ Đồ Hoạt Động (Activity Diagram): AI Hỏi Đáp & Đề Xuất Villa Thông Minh

```mermaid
flowchart TD
    Start([Khách mở hộp thoại Trợ Lý Ảo Aura AI]) --> InputMsg[Khách nhập câu hỏi / nhu cầu nghỉ dưỡng]
    InputMsg --> CleanInput[Hệ thống làm sạch văn bản & phân tích intent]
    CleanInput --> CheckCache{Câu hỏi đã có cache câu trả lời?}

    CheckCache -->|Có sẵn trong Cache| ReturnCached[Trả kết quả phản hồi siêu tốc] --> ShowAnswer
    CheckCache -->|Chưa có| FetchContext[Thu thập Context dữ liệu Resort: Villa trống, Tiện ích, Giá]

    FetchContext --> BuildPrompt[Đóng gói System Prompt: Chuyên gia Concierge 5 sao + Dữ liệu thực tế]
    BuildPrompt --> CallGemini[Gọi Google Gemini AI API]

    CallGemini --> ProcessAIResponse{Gemini trả kết quả thành công?}
    ProcessAIResponse -->|Lỗi API / Timeout| FallbackMsg[Kích hoạt kịch bản dự phòng 5 sao tiêu chuẩn] --> ShowAnswer
    ProcessAIResponse -->|Thành công| ParseCard[Bóc tách danh sách gợi ý Villa & Button đặt phòng] --> ShowAnswer

    ShowAnswer[Hiển thị câu trả lời thông minh + Thẻ Villa tương ứng]
    ShowAnswer --> UserAction{Khách click vào thẻ gợi ý?}
    UserAction -->|Xem chi tiết| OpenVillaDetail[Mở Modal chi tiết căn Biệt thự]
    UserAction -->|Đặt ngay| RedirectBooking[Điền sẵn thông tin & chuyển sang màn hình Đặt phòng]
    UserAction -->|Hỏi tiếp| InputMsg
```

### 4.2 Sơ Đồ Tuần Tự (Sequence Diagram): Luồng Xử Lý Prompt & Trả Lời Gợi Ý Biệt Thự

```mermaid
sequenceDiagram
    autonumber
    actor Guest as Khách Nghỉ Dưỡng
    participant ChatUI as Widget Chat AI
    participant AICtrl as AiChatController (/api/v1/ai/chat)
    participant AISvc as AiAgentServiceImpl
    participant VillaRepo as VillaRepository
    participant GeminiAPI as Google Gemini 1.5 Pro API

    Guest->>ChatUI: Nhập: "Gia đình tôi 4 người lớn, muốn villa view biển có hồ bơi riêng"
    ChatUI->>AICtrl: POST /api/v1/ai/chat { message, sessionId }
    AICtrl->>AISvc: askConcierge(message, sessionId)
    
    AISvc->>VillaRepo: findAvailableVillasWithPoolAndBeachView()
    VillaRepo-->>AISvc: Danh sách 3 Villa thỏa mãn (NT-101, NT-105, SB-VIP)
    
    AISvc->>AISvc: Tạo Augmented Prompt:
    Note over AISvc: "Bạn là Trưởng ban Quản gia Aura Concierge.<br/>Danh sách villa thực tế khả dụng:<br/>- NT-101: 3PN, bể bơi 30m2, view biển, 35tr/đêm<br/>- NT-105: 1PN, view biển, 25tr/đêm<br/>Hãy trả lời lịch thiệp, thuyết phục và tư vấn căn phù hợp nhất."
    
    AISvc->>GeminiAPI: generateContent(Prompt)
    GeminiAPI-->>AISvc: AI Response Text (Đề xuất căn NT-101 kèm lời chúc)
    
    AISvc->>AISvc: Gắn Metadata ID Villa vào kết quả phản hồi
    AISvc-->>AICtrl: AiChatResponse(replyText, suggestedVillas)
    AICtrl-->>ChatUI: 200 OK + Payload
    ChatUI-->>Guest: Hiển thị tin nhắn trả lời mềm mại + 3 Thẻ Villa tương tác
```

---

## PHÂN HỆ 5: NGHIỆP VỤ LỄ TÂN - CHECK-IN, CHECK-OUT & ĐẶT TRỰC TIẾP TẠI QUẦY

### 5.1 Sơ Đồ Hoạt Động (Activity Diagram): Quy Trình Tiếp Đón Check-in & Trả Phòng Check-out

```mermaid
flowchart TD
    Start([Khách đến quầy Lễ Tân]) --> ReceptionType{Loại giao dịch}

    %% Check-in
    ReceptionType -->|Khách Đã Có Đặt Phòng| SearchBooking[Tìm theo: Mã đơn / SĐT / Tên khách]
    SearchBooking --> CheckStatus{Trạng thái đơn}
    CheckStatus -->|Chưa thanh toán đủ| CollectPayment[Thu nốt tiền phòng + Tiền đặt cọc tài sản] --> CheckRoomReady
    CheckStatus -->|Đã thanh toán đủ| CheckRoomReady{Biệt thự đã dọn xong chưa?}

    CheckRoomReady -->|Đang Dọn / Bảo Trì| WaitLounge[Mời khách sang VIP Lounge dùng trà chiều chờ phòng] --> NotifyReady[Buồng phòng dọn xong báo Ready] --> DoCheckIn
    CheckRoomReady -->|Đã Sẵn Sàng Sạch 5*| DoCheckIn[Bấm 'Tiến Hành Check-in Ngay' trên hệ thống]

    DoCheckIn --> UpdateStatusCheckIn[Hệ thống đổi: Booking -> CHECKED_IN, Villa -> OCCUPIED]
    UpdateStatusCheckIn --> HandoverKey[Bàn giao chìa khóa/thẻ từ + Cử Butler dẫn đường]
    HandoverKey --> EndInHouse([Khách đang lưu trú tại Villa])

    %% Walk-in
    ReceptionType -->|Khách Vãng Lai Đặt Tại Quầy| ViewGantt[Xem sơ đồ phòng trống Gantt Timeline]
    ViewGantt --> PickAvailableRoom[Chọn biệt thự khả dụng]
    PickAvailableRoom --> FillDirectForm[Nhập thông tin khách + Thu tiền trực tiếp / Quẹt thẻ POS]
    FillDirectForm --> SaveDirect[Lưu đơn đặt trực tiếp trạng thái CHECKED_IN] --> HandoverKey

    %% Check-out
    ReceptionType -->|Khách Trả Phòng Check-out| StartCheckOut[Lễ tân bấm 'Hoàn Tất Check-out' trên Hồ sơ]
    StartCheckOut --> TriggerHKCheck[Kích hoạt lệnh Kiểm tra phòng & Minibar tức thì]
    TriggerHKCheck --> CheckMinibarLoss{Có phát sinh Minibar / Đồ dùng hư hỏng?}

    CheckMinibarLoss -->|Có phát sinh| AddBilling[Cộng tiền đồ uống/đền bù vào hóa đơn cuối] --> SettleBill
    CheckMinibarLoss -->|Không phát sinh| SettleBill[Thanh toán hóa đơn cuối cùng & Hoàn cọc]

    SettleBill --> CompleteCheckOut[Hệ thống đổi: Booking -> CHECKED_OUT, Villa -> CLEANING]
    CompleteCheckOut --> AutoCreateCleanTask[Tự động sinh nhiệm vụ Dọn phòng Checkout Deep Clean]
    AutoCreateCleanTask --> FarewellGuest[Tiễn khách ra xe đưa đón sân bay] --> EndFinished([Kết thúc kỳ nghỉ])
```

### 5.2 Sơ Đồ Tuần Tự (Sequence Diagram): Tiếp Nhận Check-in Đơn Phòng Có Sẵn

```mermaid
sequenceDiagram
    autonumber
    actor Receptionist as Nhân Viên Lễ Tân
    participant UI as Admin Room Management SPA
    participant AdminBookCtrl as AdminBookingController
    participant AdminBookSvc as AdminBookingServiceImpl
    participant VillaRepo as VillaRepository
    participant BookRepo as BookingRepository
    participant SSE as NotificationService

    Receptionist->>UI: Mở Hồ Sơ Đặt Phòng #BK-202610-NT105
    UI->>UI: Kiểm tra trạng thái đơn (CONFIRMED) & Trạng thái phòng (AVAILABLE)
    Receptionist->>UI: Bấm nút "Tiến Hành Check-in Ngay"
    UI->>AdminBookCtrl: POST /api/v1/admin/bookings/105/check-in
    AdminBookCtrl->>AdminBookSvc: performCheckIn(105, payload)

    AdminBookSvc->>BookRepo: findById(105)
    BookRepo-->>AdminBookSvc: Booking Entity

    alt Đơn không ở trạng thái CONFIRMED
        AdminBookSvc-->>AdminBookCtrl: Throw BusinessException("Chỉ đơn CONFIRMED mới được Check-in")
        AdminBookCtrl-->>UI: 400 Bad Request
        UI-->>Receptionist: Hiển thị thông báo lỗi trạng thái không hợp lệ
    else Villa chưa dọn dẹp xong (Trạng thái != AVAILABLE)
        AdminBookSvc-->>AdminBookCtrl: Throw BusinessException("Villa NT-105 chưa sẵn sàng (đang dọn). Vui lòng đợi nghiệm thu.")
        AdminBookCtrl-->>UI: 400 Bad Request
        UI-->>Receptionist: Cảnh báo buồng phòng chưa hoàn tất
    else Hợp lệ toàn bộ
        AdminBookSvc->>AdminBookSvc: booking.setStatus(CHECKED_IN), checkInTime = LocalDateTime.now()
        AdminBookSvc->>VillaRepo: updateStatus(NT-105, OCCUPIED, currentGuestName)
        AdminBookSvc->>BookRepo: save(booking)
        AdminBookSvc->>SSE: sendNotification("REFRESH_GANTT")
        AdminBookSvc-->>AdminBookCtrl: BookingResponse
        AdminBookCtrl-->>UI: 200 OK ("Thực hiện Check-in thành công")
        UI->>UI: Cập nhật thẻ Villa sang màu Đang Có Khách (Amber)
        UI-->>Receptionist: Hiển thị Toast thông báo thành công và in Thẻ Chào Mừng
    end
```

### 5.3 Sơ Đồ Tuần Tự (Sequence Diagram): Trả Phòng (Check-out) & Kích Hoạt Dọn Dẹp

```mermaid
sequenceDiagram
    autonumber
    actor Receptionist as Lễ Tân Quầy
    participant UI as Admin Room Management Modal
    participant BookCtrl as AdminBookingController
    participant BookSvc as AdminBookingServiceImpl
    participant VillaRepo as VillaRepository
    participant TaskRepo as HousekeepingTaskRepository
    participant SSE as NotificationService

    Receptionist->>UI: Bấm "Hoàn Tất Check-out & Trả Phòng"
    UI->>UI: Xác nhận hộp thoại nghiệm thu minibar & thanh toán
    UI->>BookCtrl: POST /api/v1/admin/bookings/105/check-out
    BookCtrl->>BookSvc: checkOutBooking(105)

    BookSvc->>BookSvc: booking.setStatus(CHECKED_OUT), checkOutTime = now()
    BookSvc->>VillaRepo: updateStatus(NT-105, CLEANING, currentGuestName = null)
    
    BookSvc->>TaskRepo: save(HousekeepingTask: taskType=CHECKOUT_DEEP, status=PENDING, priority=HIGH)
    TaskRepo-->>BookSvc: Task Created (ID = 502)

    BookSvc->>SSE: sendNotification("NEW_CLEANING_TASK_DISPATCHED", villaNumber="NT-105")
    SSE-->>UI: Cập nhật sơ đồ phòng: Villa NT-105 chuyển sang màu Xanh (Đang dọn)
    
    BookSvc-->>BookCtrl: BookingResponse
    BookCtrl-->>UI: 200 OK ("Hoàn tất thủ tục Check-out cho biệt thự NT-105")
    UI-->>Receptionist: Thông báo thành công, villa đã được chuyển cho đội Buồng Phòng
```

---

## PHÂN HỆ 6: QUẢN TRỊ BUỒNG PHÒNG, QUY TRÌNH DỌN 16 BƯỚC & MOBILE PORTAL

### 6.1 Sơ Đồ Hoạt Động (Activity Diagram): Vòng Đời Buồng Phòng (Cleaning Lifecycle)

```mermaid
flowchart TD
    StartTask([Phòng chuyển trạng thái DỌN DẸP / CLEANING]) --> AutoTask[Hệ thống tự động sinh nhiệm vụ Dọn dẹp]
    AutoTask --> DispatchType{Phương thức phân công}

    DispatchType -->|Trưởng ca phân công thủ công| ManualAssign[Trưởng ca chọn nhân viên ca trực trên Portal]
    DispatchType -->|Thuật toán chia ca tự động| AutoBalance[Tự động gán cho nhân viên có ít task nhất]

    ManualAssign --> NotifyHK[Gửi thông báo rung chuông về điện thoại nhân viên]
    AutoBalance --> NotifyHK

    NotifyHK --> HKAccept[Nhân viên mở Mobile Portal & bấm 'BẮT ĐẦU DỌN']
    HKAccept --> TimerStart[Ghi nhận giờ bắt đầu • Trạng thái: IN_PROGRESS]

    TimerStart --> StepLoop[Thực hiện 16 Bước Chuẩn 5 Sao: Ga gối, Hút bụi, Ozon, Khử trùng, Setup Amenities]
    StepLoop --> HasDamage{Phát hiện hỏng hóc đồ hoặc khách bỏ quên?}

    HasDamage -->|Có đồ hỏng| ReportMaint[Chụp ảnh & Gửi lệnh sửa chữa bảo trì khẩn]
    HasDamage -->|Có đồ bỏ quên| ReportLostFound[Chụp ảnh & Lưu kho đồ thất lạc Lost & Found]
    HasDamage -->|Không| CheckMinibar

    ReportMaint --> CheckMinibar{Khách có dùng đồ Minibar?}
    ReportLostFound --> CheckMinibar

    CheckMinibar -->|Có dùng| InputMini[Nhập số lượng đồ uống đã uống & chụp ảnh bill] --> Finish16Steps
    CheckMinibar -->|Không dùng| Finish16Steps[Tích đủ 16 bước checklist dọn dẹp]

    Finish16Steps --> ClickComplete[Nhân viên bấm 'HOÀN THÀNH NHIỆM VỤ']
    ClickComplete --> TaskStateWaitingQC[Nhiệm vụ chuyển sang: WAITING_INSPECTION]

    TaskStateWaitingQC --> QCInspection[Trưởng ca / Giám sát buồng phòng đến kiểm tra thực tế]
    QCInspection --> QualityCheck{Chất lượng đạt chuẩn?}

    QualityCheck -->|Chưa đạt| RejectQC[Bấm 'Yêu cầu dọn lại' + Ghi chú lỗi] --> HKAccept
    QualityCheck -->|Đạt chuẩn 5 sao| ApproveQC[Bấm 'Phê Duyệt Nghiệm Thu QC']

    ApproveQC --> ReleaseRoom[Hệ thống đổi trạng thái Villa: SẴN SÀNG / AVAILABLE]
    ReleaseRoom --> EndRoomReady([Biệt thự sẵn sàng đón khách mới])
```

### 6.2 Sơ Đồ Tuần Tự (Sequence Diagram): Nhân Viên Thao Tác Mobile Portal & Nghiệm Thu QC

```mermaid
sequenceDiagram
    autonumber
    actor HK as Nhân Viên Buồng Phòng
    participant Mobile as Mobile Web App (Housekeeping)
    participant HKCtrl as HousekeepingMobileController
    participant HKSvc as HousekeepingServiceImpl
    participant AdminHKCtrl as AdminHousekeepingController
    actor Supervisor as Trưởng Ca / QC Inspector
    participant DB as MySQL Database

    HK->>Mobile: Mở danh sách ca trực (/housekeeping/my-tasks)
    Mobile->>HKCtrl: GET /api/v1/housekeeping/my-tasks
    HKCtrl->>HKSvc: getTasksByStaffId(currentUserId)
    HKSvc-->>Mobile: Danh sách: Villa NT-105 (Deep Clean)

    HK->>Mobile: Nhấn "Bắt Đầu Dọn Phòng"
    Mobile->>HKCtrl: POST /api/v1/housekeeping/tasks/502/start
    HKCtrl->>HKSvc: startTask(502)
    HKSvc->>DB: UPDATE housekeeping_tasks SET status='IN_PROGRESS', start_time=NOW()
    HKSvc-->>Mobile: 200 OK (Bắt đầu tính giờ đồng hồ bấm giờ)

    loop 16 Bước Vệ Sinh Chuẩn 5 Sao
        HK->>Mobile: Tích chọn hoàn thành từng bước (1..16)
        Mobile->>HKCtrl: POST /api/v1/housekeeping/tasks/502/progress { stepId, checked: true }
    end

    HK->>Mobile: Nhấn "Hoàn Thành & Chờ Nghiệm Thu"
    Mobile->>HKCtrl: POST /api/v1/housekeeping/tasks/502/complete
    HKCtrl->>HKSvc: markTaskComplete(502)
    HKSvc->>DB: UPDATE housekeeping_tasks SET status='WAITING_INSPECTION', completed_at=NOW()
    HKSvc-->>Mobile: 200 OK (Chuyển sang màn hình chờ duyệt)

    Supervisor->>AdminHKCtrl: POST /api/v1/admin/housekeeping/tasks/502/inspect { result: 'PASSED', rating: 5 }
    AdminHKCtrl->>HKSvc: inspectTask(502, result)
    HKSvc->>DB: UPDATE housekeeping_tasks SET status='COMPLETED', inspected_by=...
    HKSvc->>DB: UPDATE villas SET status='AVAILABLE', ozone_status='STERILIZED' WHERE id=105
    HKSvc-->>AdminHKCtrl: 200 OK (QC nghiệm thu đạt chuẩn)
    AdminHKCtrl-->>Supervisor: Đã mở khóa phòng NT-105 đón khách
```

---

## PHÂN HỆ 7: KIỂM KÊ MINIBAR, BỔ SUNG KHO & ĐỐI SOÁT HÓA ĐƠN

### 7.1 Sơ Đồ Hoạt Động (Activity Diagram): Ghi Nhận Tiêu Thụ, Bổ Sung & Tính Hóa Đơn

```mermaid
flowchart TD
    Start([Kiểm tra Minibar khi khách Check-out]) --> OpenFridge[Nhân viên kiểm tra tủ lạnh & giỏ snack]
    OpenFridge --> CompareStd[So sánh với Định mức tiêu chuẩn ban đầu]
    CompareStd --> HasConsumption{Có hao hụt số lượng?}

    HasConsumption -->|Không| ReportZero[Ghi nhận tiêu thụ = 0₫] --> CleanBill([Hóa đơn minibar sạch])
    HasConsumption -->|Có tiêu thụ| RecordItems[Chọn danh mục món: Vang đỏ, Bia thủ công, Nước suối Fiji, Hạt điều]

    RecordItems --> AttachEvidence[Chụp ảnh chai rỗng / bao bì làm bằng chứng]
    AttachEvidence --> SubmitConsumption[Gửi biên bản tiêu thụ về Lễ Tân & Kế Toán]

    SubmitConsumption --> SystemCalc[Hệ thống tự động tra bảng giá & tính tổng tiền]
    SystemCalc --> NotifyFrontDesk[Hiển thị cảnh báo màu đỏ trên màn hình Check-out của Lễ Tân]

    NotifyFrontDesk --> Reconcile{Khách xác nhận tiêu thụ?}
    Reconcile -->|Khách đồng ý| AddToTotalBill[Cộng trực tiếp vào hóa đơn thanh toán cuối cùng]
    Reconcile -->|Khách thắc mắc| ViewPhotoProof[Mở ảnh bằng chứng do Buồng phòng chụp] --> GuestAgrees[Khách xác nhận thanh toán] --> AddToTotalBill

    AddToTotalBill --> CreateRefillTicket[Tự động sinh Lệnh Bổ Sung Hàng cho Villa]
    CreateRefillTicket --> WarehouseDispatch[Kho xuất hàng bổ sung cho đầy định mức tủ lạnh]
    WarehouseDispatch --> DeductStock[(Trừ số lượng tồn kho vật tư trong Database)]
    DeductStock --> RefillDone([Tủ minibar đầy đủ sẵn sàng đón khách tiếp theo])
```

### 7.2 Sơ Đồ Tuần Tự (Sequence Diagram): Buồng Phòng Báo Tiêu Thụ -> Kế Toán Đối Soát Chốt Bill

```mermaid
sequenceDiagram
    autonumber
    actor HK as Nhân Viên Buồng Phòng
    participant Mobile as Mobile App
    participant ConsCtrl as HousekeepingMobileController
    participant BillingCtrl as AdminBillingConsumptionController
    participant BillingSvc as BillingConsumptionServiceImpl
    participant InvRepo as InventoryRepository
    participant DB as MySQL Database
    actor Cashier as Thu Ngân / Kế Toán

    HK->>Mobile: Quét phòng NT-105: Khách uống 2 Bia Heineken, 1 Hạt Macca
    Mobile->>ConsCtrl: POST /api/v1/housekeeping/tasks/502/consumptions
    Note over Mobile,ConsCtrl: Payload: [{ item: 'Heineken', qty: 2 }, { item: 'Macca', qty: 1 }]
    ConsCtrl->>BillingSvc: recordConsumptions(taskId, bookingId, items)
    
    BillingSvc->>DB: INSERT INTO room_consumptions (booking_id, item_name, quantity, price, status='PENDING')
    BillingSvc-->>ConsCtrl: 201 Created (+230.000₫)
    ConsCtrl-->>Mobile: Đã ghi nhận tiêu thụ thành công

    Cashier->>BillingCtrl: GET /api/v1/admin/billing/consumptions/booking/105
    BillingCtrl->>BillingSvc: getConsumptionsByBooking(105)
    BillingSvc-->>BillingCtrl: Danh sách 2 món chờ chốt (+230.000₫)
    BillingCtrl-->>Cashier: Hiển thị popup "Minibar phát sinh +230.000₫"

    Cashier->>Cashier: In bill tổng hợp gửi khách ký nhận
    Cashier->>BillingCtrl: POST /api/v1/admin/billing/consumptions/reconcile { bookingId: 105 }
    BillingCtrl->>BillingSvc: reconcileAndClose(105)
    
    BillingSvc->>DB: UPDATE room_consumptions SET status='APPROVED', reconciled_at=NOW()
    BillingSvc->>InvRepo: deductStock(villaId=105, items)
    InvRepo->>DB: UPDATE inventory_items SET stock = stock - qty
    BillingSvc-->>BillingCtrl: 200 OK ("Đã chốt xong chi phí minibar")
    BillingCtrl-->>Cashier: Hoàn tất đối soát, in phiếu thu tiền
```

---

## PHÂN HỆ 8: ĐIỀU PHỐI PHƯƠNG TIỆN VIP & DỊCH VỤ ĐỘC BẢN

### 8.1 Sơ Đồ Hoạt Động (Activity Diagram): Đón Tiễn Maybach, Du Thuyền & Trực Thăng

```mermaid
flowchart TD
    Start([Đơn đặt phòng VIP có dịch vụ di chuyển cao cấp]) --> DetectVIP{Loại phương tiện yêu cầu}

    DetectVIP -->|Xe Sang Maybach S680| CarFlow[Điều phối xe Maybach & Tài xế riêng]
    DetectVIP -->|Du Thuyền Aura Pearl| YachtFlow[Điều phối Hải trình & Thuyền trưởng]
    DetectVIP -->|Trực Thăng Bell 505| HeliFlow[Liên hệ bãi đáp Helipad & Cơ trưởng]
    DetectVIP -->|Xe Buggy Nội Khu| BuggyFlow[Cử xe điện Buggy đón khách tại sảnh]

    CarFlow --> AssignButler[Chỉ định Lead Butler phụ trách tiếp đón 24/7]
    YachtFlow --> AssignButler
    HeliFlow --> AssignButler
    BuggyFlow --> AssignButler

    AssignButler --> CheckFlight[Cập nhật thông tin Chuyến bay & Giờ hạ cánh thực tế]
    CheckFlight --> Depart[Tài xế & Quản gia lên đường đón khách trước 30 phút]
    Depart --> StatusDispatch[Cập nhật trạng thái: ĐANG DI CHUYỂN]

    StatusDispatch --> MeetGuest[Gặp khách tại Ga đến: Cầm biển đón + Khăn lạnh + Nước suối]
    MeetGuest --> EscortCar[Hỗ trợ hành lý lên xe & Mở điều hòa nhiệt độ yêu cầu]
    EscortCar --> ArriveResort[Về tới cổng Resort: Báo trước Lễ Tân 5 phút]
    ArriveResort --> DirectCheckIn[Khách không cần ghé quầy • Check-in trực tiếp tại Biệt Thự]
    DirectCheckIn --> FinishDispatch([Hoàn thành lệnh điều phối VIP])
```

### 8.2 Sơ Đồ Tuần Tự (Sequence Diagram): Lập Lệnh Điều Phối & Bàn Giao Quản Gia Butler

```mermaid
sequenceDiagram
    autonumber
    actor Concierge as Nhân Viên VIP Concierge
    participant UI as Admin Service Dispatch SPA
    participant DispatchCtrl as AdminServiceDispatchController
    participant DispatchSvc as AdminServiceDispatchServiceImpl
    participant Driver as Tài Xế Maybach / Thuyền Trưởng
    participant Butler as Lead Butler Phụ Trách
    participant DB as MySQL Database

    Concierge->>UI: Mở đơn khách VIP Long Phùng (#BK-NT105)
    Concierge->>UI: Bấm "Tạo Lệnh Điều Phối Xe VIP Maybach S680"
    Note over Concierge,UI: Nhập: Biển số #01, Giờ hạ cánh 13:15, Chuyến bay VN1234
    
    UI->>DispatchCtrl: POST /api/v1/admin/services/dispatches (DispatchPayload)
    DispatchCtrl->>DispatchSvc: createDispatchOrder(payload)
    DispatchSvc->>DB: INSERT INTO service_dispatches (vehicle, driver, butler, flight, status='ASSIGNED')
    DB-->>DispatchSvc: Dispatch Order Created (ID = 88)
    
    DispatchSvc-->>DispatchCtrl: 201 Created
    DispatchCtrl-->>UI: Hiển thị Thẻ Điều Phối Thành Công

    par Thông báo Tài xế & Quản gia
        DispatchSvc->>Driver: SMS / Notif: "Đón khách Long Phùng lúc 13:15 tại Sân bay"
        DispatchSvc->>Butler: SMS / Notif: "Phân công Lead Butler cho khách căn NT-105"
    end

    Driver->>UI: Bấm "Bắt Đầu Lên Đường Đón Khách"
    UI->>DispatchCtrl: PUT /api/v1/admin/services/dispatches/88/status { status: 'EN_ROUTE' }
    DispatchCtrl->>DispatchSvc: updateStatus(88, EN_ROUTE)
    DispatchSvc->>DB: UPDATE service_dispatches SET status='EN_ROUTE', departure_time=NOW()
    DispatchSvc-->>UI: 200 OK (Cập nhật thời gian thực trên bản đồ điều phối)
```

---

## PHÂN HỆ 9: ĐÁNH GIÁ CSAT, NPS & QUY TRÌNH CỨU VÃN KHIẾU NẠI

### 9.1 Sơ Đồ Hoạt Động (Activity Diagram): Thu Thập Đánh Giá & Kích Hoạt Ticket Khẩn Cấp

```mermaid
flowchart TD
    Start([Khách hoàn tất thủ tục trả phòng]) --> AutoSendReview[Hệ thống gửi Email / SMS mời đánh giá kỳ nghỉ]
    AutoSendReview --> GuestReviewForm[Khách mở liên kết đánh giá: Chấm sao & Viết nhận xét]
    GuestReviewForm --> SubmitReview[Khách bấm Gửi Đánh Giá]

    SubmitReview --> ScoreCheck{Số sao đánh giá}

    ScoreCheck -->|4 - 5 Sao Hài Lòng| PositivePath[Đánh giá tích cực: Tính điểm CSAT & NPS +86]
    PositivePath --> AutoThankYou[Gửi thư cảm ơn + Tặng voucher ưu đãi kỳ nghỉ tiếp theo] --> EndNormal([Hoàn tất đánh giá])

    ScoreCheck -->|1 - 3 Sao Khiếu Nại| AlarmPath[HỆ THỐNG PHÁT CHUÔNG BÁO ĐỘNG ĐỎ]
    AlarmPath --> CreateUrgentTicket[Tự động tạo Ticket Cứu Vãn Khẩn Cấp • SLA < 3 phút]
    CreateUrgentTicket --> PushManager[Bắn thông báo tức thì đến Giám Đốc Trực & Quản Lý Sảnh]

    PushManager --> ContactGuest[Quản lý liên hệ ngay qua điện thoại / gặp trực tiếp]
    ContactGuest --> ResolveAction[Thực hiện giải pháp cứu vãn: Đổi phòng, Miễn phí bữa tối, Tặng rượu vang]

    ResolveAction --> UpdateTicketStatus[Cập nhật biên bản xử lý vào hệ thống]
    UpdateTicketStatus --> ReRate{Khách có hài lòng với cách xử lý?}
    ReRate -->|Hài lòng| UpgradeScore[Khách cập nhật lại đánh giá lên 5 sao] --> EndResolved([Cứu vãn thành công])
    ReRate -->|Chưa hài lòng| EscalateGM[Chuyển tiếp báo cáo trực tiếp Tổng Giám Đốc GM] --> EndEscalate([Theo dõi đặc biệt])
```

### 9.2 Sơ Đồ Tuần Tự (Sequence Diagram): Gửi Đánh Giá -> Báo Động Khiếu Nại -> Xử Lý SLA < 3 Phút

```mermaid
sequenceDiagram
    autonumber
    actor Guest as Khách Hàng
    participant ReviewUI as Angular Review Component
    participant RevCtrl as ReviewController
    participant RevSvc as ReviewServiceImpl
    participant RecoveryCtrl as AdminReviewRecoveryController
    participant SSE as NotificationService
    actor DutyManager as Giám Đốc Trực Ca (Duty Manager)
    participant DB as MySQL Database

    Guest->>ReviewUI: Chấm 2 Sao + Nhận xét: "Điều hòa phòng khách kêu to lúc nửa đêm"
    ReviewUI->>RevCtrl: POST /api/v1/reviews { bookingId, rating: 2, comment: "..." }
    RevCtrl->>RevSvc: saveReview(dto)
    RevSvc->>DB: INSERT INTO reviews (rating=2, comment=..., status='FLAGGED')

    RevSvc->>RevSvc: Phát hiện rating <= 3: Kích hoạt Trigger Khẩn Cấp
    RevSvc->>DB: INSERT INTO recovery_tickets (review_id, sla_minutes=3, status='OPEN')
    RevSvc->>SSE: sendUrgentAlert("CSAT_EMERGENCY_ALERT", "Khách căn NT-105 chấm 2 sao")
    
    SSE-->>DutyManager: Chuông báo động reo trên màn hình Admin kèm đếm ngược 03:00

    DutyManager->>DutyManager: Bấm mở Ticket & Xem số điện thoại khách hàng (+84 918 223 999)
    DutyManager->>Guest: Gọi điện thoại xin lỗi trong vòng 90 giây & cử Kỹ thuật đến kiểm tra
    Note over DutyManager,Guest: Tặng miễn phí bữa tối hải sản BBQ bãi biển trị giá 3.500.000₫

    DutyManager->>RecoveryCtrl: PUT /api/v1/admin/reviews/recovery-tickets/45 { resolution: "Đã xử lý điều hòa và tặng BBQ", guestSatisfied: true }
    RecoveryCtrl->>DB: UPDATE recovery_tickets SET status='RESOLVED', resolved_at=NOW()
    RecoveryCtrl-->>DutyManager: 200 OK ("Ticket đã được đóng thành công trong 2 phút 15 giây")
```

---

## PHÂN HỆ 10: XẾP CA TRỰC 24/7, ĐỔI CA & KHÓA SỔ CUỐI NGÀY

### 10.1 Sơ Đồ Hoạt Động (Activity Diagram): Xếp Lịch, Đổi Ca & Khóa Sổ Cuối Ngày

```mermaid
flowchart TD
    Start([Vận hành nhân sự & Kế toán]) --> SubProcess{Quy trình nghiệp vụ}

    %% Xếp ca & Đổi ca
    SubProcess -->|Quản Lý Nhân Sự & Ca Trực| RosterView[Xem ma trận phân ca: Ca Sáng 06-14h, Chiều 14-22h, Đêm 22-06h]
    RosterView --> StaffRequest{Nhân viên cần đổi ca?}
    StaffRequest -->|Có nhu cầu| SubmitSwap[Gửi yêu cầu đổi ca trực trên hệ thống]
    SubmitSwap --> ManagerReview{Trưởng ca phê duyệt?}
    ManagerReview -->|Từ chối| RejectSwap[Thông báo từ chối kèm lý do thiếu người] --> RosterView
    ManagerReview -->|Đồng ý| ApproveSwap[Hệ thống tự động hoán đổi lịch trực giữa 2 nhân sự] --> UpdateRoster[Cập nhật bảng chấm công thời gian thực]

    %% Khóa sổ cuối ngày Night Audit
    SubProcess -->|Kiểm Toán Đêm Night Audit| NightAuditStart[00:00 - Kế toán viên chạy quy trình Night Audit]
    NightAuditStart --> Step1[1. Quét đối soát toàn bộ tiền phòng và minibar trong ngày]
    Step1 --> Step2[2. Khớp tiền mặt tại két với sao kê ngân hàng MoMo, VNPay, POS quẹt thẻ]
    Step2 --> DiscrepancyCheck{Có phát sinh chênh lệch số dư?}

    DiscrepancyCheck -->|Có chênh lệch| AuditAlert[Đánh dấu bút toán bất thường • Yêu cầu thu ngân giải trình] --> ManualFix[Kế toán trưởng duyệt xử lý điều chỉnh] --> Step3
    DiscrepancyCheck -->|Khớp 100%| Step3[3. Tự động chuyển toàn bộ doanh thu vào Sổ Cái Kép]

    Step3 --> Step4[4. Chốt số dư cuối ngày & Khóa cổng chỉnh sửa hóa đơn cũ]
    Step4 --> ExportReport[5. Tự động xuất Báo cáo Tài chính CSV/PDF gửi Ban Giám Đốc]
    ExportReport --> EndDayClosing([Hoàn tất khóa sổ - Khởi tạo ngày kinh doanh mới])
```

### 10.2 Sơ Đồ Tuần Tự (Sequence Diagram): Yêu Cầu Đổi Ca & Quản Lý Phê Duyệt

```mermaid
sequenceDiagram
    autonumber
    actor StaffA as Nhân Viên A (Phạm Văn Minh)
    actor StaffB as Nhân Viên B (Long Phùng)
    participant StaffUI as Portal Nhân Sự
    participant StaffCtrl as AdminStaffController
    participant StaffSvc as AdminStaffServiceImpl
    actor Manager as Quản Lý Ca Trực
    participant DB as MySQL Database

    StaffA->>StaffUI: Tạo yêu cầu đổi ca: Muốn đổi Ca Sáng ngày 06/10 với Nhân viên B
    StaffUI->>StaffCtrl: POST /api/v1/admin/staff/swap-requests { targetStaffId: B, targetShiftId: 102 }
    StaffCtrl->>StaffSvc: requestShiftSwap(payload)
    StaffSvc->>DB: INSERT INTO shift_swap_requests (from_user, to_user, status='PENDING_PEER')
    StaffSvc-->>StaffB: Gửi thông báo: "Bạn có yêu cầu đổi ca từ Phạm Văn Minh"

    StaffB->>StaffUI: Bấm "Đồng ý hoán đổi"
    StaffUI->>StaffCtrl: PUT /api/v1/admin/staff/swap-requests/12/peer-accept
    StaffCtrl->>StaffSvc: peerAccept(12)
    StaffSvc->>DB: UPDATE shift_swap_requests SET status='PENDING_MANAGER'
    StaffSvc-->>Manager: Bắn thông báo: "Chờ Quản lý duyệt đổi ca giữa Minh và Long"

    Manager->>StaffUI: Mở danh sách duyệt đổi ca & bấm "PHÊ DUYỆT"
    StaffUI->>StaffCtrl: POST /api/v1/admin/staff/swap-requests/12/approve
    StaffCtrl->>StaffSvc: approveShiftSwap(12)
    StaffSvc->>DB: UPDATE staff_schedules SET user_id=B WHERE shift_id=101
    StaffSvc->>DB: UPDATE staff_schedules SET user_id=A WHERE shift_id=102
    StaffSvc->>DB: UPDATE shift_swap_requests SET status='APPROVED'
    StaffSvc-->>StaffCtrl: 200 OK ("Hoán đổi ca trực thành công")
    StaffCtrl-->>StaffUI: Cập nhật Lịch Roster mới ngay lập tức
```

### 10.3 Sơ Đồ Tuần Tự (Sequence Diagram): Quy Trình Kiểm Toán Đêm & Khóa Sổ Đối Soát (Night Audit)

```mermaid
sequenceDiagram
    autonumber
    actor Auditor as Kiểm Toán Viên Đêm / Kế Toán
    participant AuditUI as Màn Hình Sổ Cái & Báo Cáo
    participant LedgerCtrl as AdminLedgerController
    participant LedgerSvc as AdminLedgerServiceImpl
    participant PaymentRepo as PaymentRepository
    participant DB as MySQL Database

    Auditor->>AuditUI: Chọn ngày làm việc và nhấn "Khởi Chạy Kiểm Toán Đêm (Day-End Closing)"
    AuditUI->>LedgerCtrl: POST /api/v1/admin/payments/day-end-closing { date: '2026-10-05' }
    LedgerCtrl->>LedgerSvc: executeNightAudit(date)

    LedgerSvc->>PaymentRepo: aggregateDailyRevenue(date)
    PaymentRepo->>DB: SELECT payment_method, SUM(amount) FROM payments WHERE DATE(created_at) = ? GROUP BY payment_method
    DB-->>PaymentRepo: [VNPay: 120M, MoMo: 85M, Tiền mặt: 45.5M, POS: 0M]

    LedgerSvc->>LedgerSvc: So khớp với dữ liệu Sổ Cái Kép (Debit / Credit)
    LedgerSvc->>DB: INSERT INTO ledger_entries (account='REVENUE_ROOM', type='CREDIT', amount=250500000)
    LedgerSvc->>DB: INSERT INTO daily_closing_audits (closing_date=?, total_revenue=250500000, status='LOCKED')
    
    LedgerSvc->>LedgerSvc: Đóng sổ ngày 05/10/2026 • Khóa quyền sửa các hóa đơn phát sinh trước đó
    LedgerSvc-->>LedgerCtrl: NightAuditSummary(totalVillas=16, occupancyRate=88%, totalRevenue=250500000)
    LedgerCtrl-->>AuditUI: 200 OK + Biên Bản Kiểm Toán Đêm Thành Công
    AuditUI-->>Auditor: Hiển thị báo cáo tài chính chốt ngày + Xuất file PDF/Excel hoàn tất
```

---

## BẢNG TỔNG HỢP MÃ API VÀ CHỨC NĂNG TƯƠNG ỨNG

| STT | Nhóm Nghiệp Vụ | API Endpoint Chính | HTTP Method | Chức Năng Cốt Lõi |
| :--- | :--- | :--- | :--- | :--- |
| **1** | Xác Thực | `/api/v1/auth/login` | `POST` | Đăng nhập cấp phát JWT Access Token |
| **2** | Xác Thực | `/api/v1/auth/register` | `POST` | Đăng ký tài khoản khách hàng mới |
| **3** | Xác Thực | `/api/v1/auth/forgot-password` | `POST` | Gửi mã OTP 6 số khôi phục mật khẩu qua Email |
| **4** | Đặt Phòng | `/api/v1/public/villas` | `GET` | Tìm kiếm biệt thự theo ngày lưu trú và số khách |
| **5** | Đặt Phòng | `/api/v1/bookings` | `POST` | Khởi tạo đơn đặt phòng trực tuyến |
| **6** | Thanh Toán | `/api/v1/payments/create-momo` | `POST` | Khởi tạo liên kết thanh toán MoMo QR / Thẻ |
| **7** | Thanh Toán | `/api/v1/payments/momo-ipn` | `POST` | Webhook IPN nhận kết quả thanh toán tự động |
| **8** | AI Concierge | `/api/v1/ai/chat` | `POST` | Trợ lý ảo AI tư vấn chọn phòng & dịch vụ |
| **9** | Lễ Tân | `/api/v1/admin/bookings/direct` | `POST` | Tạo đơn đặt phòng trực tiếp tại quầy |
| **10** | Lễ Tân | `/api/v1/admin/bookings/{id}/check-in` | `POST` | Thực hiện thủ tục nhận phòng (Check-in) |
| **11** | Lễ Tân | `/api/v1/admin/bookings/{id}/check-out` | `POST` | Hoàn tất thủ tục trả phòng (Check-out) |
| **12** | Buồng Phòng | `/api/v1/housekeeping/my-tasks` | `GET` | Lấy danh sách phòng phân công ca trực di động |
| **13** | Buồng Phòng | `/api/v1/housekeeping/tasks/{id}/progress` | `POST` | Cập nhật tiến độ 16 bước checklist buồng phòng |
| **14** | Buồng Phòng | `/api/v1/admin/housekeeping/tasks/{id}/inspect` | `POST` | Giám sát viên phê duyệt nghiệm thu chất lượng QC |
| **15** | Minibar & Kho | `/api/v1/housekeeping/tasks/{id}/consumptions` | `POST` | Báo cáo đồ uống minibar khách đã sử dụng |
| **16** | Minibar & Kho | `/api/v1/admin/billing/consumptions/{id}/reconcile` | `POST` | Kế toán đối soát và chốt hóa đơn minibar |
| **17** | VIP Concierge| `/api/v1/admin/services/dispatches` | `POST` | Lập lệnh điều phối xe Maybach, Du thuyền, Buggy |
| **18** | Đánh Giá & CSAT| `/api/v1/reviews` | `POST` | Khách gửi đánh giá chấm sao và nhận xét |
| **19** | Đánh Giá & CSAT| `/api/v1/admin/reviews/recovery-tickets` | `POST` | Kích hoạt ticket cứu vãn khẩn cấp SLA < 3 phút |
| **20** | Nhân Sự & Ca | `/api/v1/admin/staff/swap-requests/{id}/approve` | `POST` | Quản lý phê duyệt yêu cầu đổi ca trực |
| **21** | Kế Toán Sổ Cái | `/api/v1/admin/payments/day-end-closing` | `POST` | Kiểm toán đêm và khóa sổ đối soát cuối ngày |

---

> **Ghi Chú Triển Khai & Kiểm Thử:**  
> Toàn bộ các sơ đồ Mermaid trong tài liệu này tuân thủ cú pháp chuẩn quốc tế, có thể hiển thị trực tiếp trên Visual Studio Code (Markdown Preview Mermaid Support), GitHub Markdown, Notion hoặc sao chép vào [Mermaid Live Editor](https://mermaid.live) để kết xuất dưới dạng ảnh SVG/PNG vector độ phân giải cao phục vụ thuyết trình hoặc báo cáo kiến trúc hệ thống.
