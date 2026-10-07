# 🌴 Aura Resort – Luxury Hotel & Villa Booking Platform

[![Java](https://img.shields.io/badge/Java-17-orange.svg?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red.svg?logo=angular)](https://angular.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?logo=mysql)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-dc382d.svg?logo=redis)](https://redis.io/)
[![Ollama](https://img.shields.io/badge/AI%20Agent-Ollama%20%7C%20Qwen2.5-black.svg?logo=openai)](https://ollama.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg?logo=docker)](https://www.docker.com/)
[![AWS EC2](https://img.shields.io/badge/Deploy-AWS%20EC2-FF9900.svg?logo=amazonec2)](https://aws.amazon.com/ec2/)

> **Aura Resort** là nền tảng quản lý và đặt phòng biệt thự nghỉ dưỡng 5 sao cao cấp (Sầm Sơn). Hệ thống được thiết kế theo kiến trúc Monorepo hiện đại, tích hợp **Trợ lý AI thông minh qua cơ chế Tool Calling (Function Calling)**, cổng thanh toán trực tuyến **MoMo**, phân quyền chặt chẽ **Spring Security (JWT)** và triển khai toàn bộ bằng **Docker Compose trên AWS EC2**.

---

## 🌐 Live Demo & Credentials

* **Trang chủ Website:** [http://47.129.128.116/](http://47.129.128.116/)
* **Tài khoản trải nghiệm:**
  * **Khách hàng (Customer):** Đăng ký trực tiếp trên web hoặc sử dụng tài khoản Google.
  * **Quản trị viên (Admin):** Đăng nhập tại `/auth/login` với quyền quản lý phòng, doanh thu, nhân sự và điều phối buồng phòng.

---

## 🚀 Tính Năng Nổi Bật (Key Features)

### 1. 🤖 AI Agent Tư Vấn & Đặt Phòng (Tool Calling / Function Calling)
* **Tích hợp LLM nội bộ:** Ứng dụng mô hình ngôn ngữ lớn **Qwen 2.5 (3B)** chạy qua **Ollama** trong mạng lưới container.
* **Cơ chế Tool Calling thông minh:** AI không chỉ trả lời văn bản thông thường, mà tự động phân tích intent của khách để trích xuất ngày check-in/out, số lượng khách, khu vực và thực thi các function Java kết nối trực tiếp database:
  * `searchAvailableVillas`: Tra cứu phòng trống thực tế theo thời gian thực.
  * `getVillaDetails`: Lấy chi tiết thông số, tiện ích và giá phòng.
  * `createBooking`: Hỗ trợ tạo đơn đặt phòng trực tiếp cho khách hàng đã đăng nhập.
  * `getComboPackages`: Tư vấn gói ưu đãi, trăng mật, sinh nhật.
* **Tương tác trực quan:** Tự động kết xuất các **Thẻ Villa (Villa Cards)** kèm hình ảnh thực tế, tiện ích và nút "Đặt ngay" ngay trong cửa sổ chat.

### 2. 🏨 Hệ Thống Quản Lý Biệt Thự & Đặt Phòng (Booking Engine)
* **Phân khu nghỉ dưỡng:** Quản lý 3 phân khu chủ đạo: **Ngọc Trai** (bãi biển riêng), **Sao Biển** (view panorama, jacuzzi), **San Hô** (yên tĩnh, gia đình).
* **Kiểm soát phòng trống chặt chẽ:** Thuật toán chống xung đột lịch (**Double-booking prevention**) kiểm tra chéo ngày check-in và check-out theo thời gian thực.
* **Lịch biểu & Tự động hủy đơn quá hạn:** Tích hợp Spring Scheduled Cron Job tự động rà soát và giải phóng phòng với các đơn đặt quá hạn thanh toán.

### 3. 💳 Tích Hợp Cổng Thanh Toán MoMo
* Tích hợp quy trình thanh toán MoMo Sandbox qua mã QR và thẻ tín dụng.
* Cơ chế xác thực chữ ký mã hóa **HMAC-SHA256** đảm bảo an toàn giao dịch.
* Xử lý webhook bất đồng bộ qua **IPN (Instant Payment Notification)** với cơ chế Idempotency ngăn chặn ghi trùng đơn hàng.

### 4. 🔐 Bảo Mật & Phân Quyền (Security & RBAC)
* Xác thực không trạng thái (**Stateless JWT Authentication**) với Access Token & Refresh Token.
* Phân quyền Role-based Access Control (**RBAC**): `ADMIN`, `STAFF`, `USER`.
* Bảo vệ đa tầng: Spring Security Filter Chain ở Backend + Angular Route Guards (`canActivate`) ở Frontend.

### 5. ⚡ Hiệu Năng & Vận Hành
* **Redis Caching:** Lưu trữ đệm danh mục phòng, thông tin khu vực và banner giúp giảm tải truy vấn MySQL và tối ưu tốc độ phản hồi.
* **Housekeeping Portal:** Cổng quản lý trạng thái vệ sinh buồng phòng (Cleaned, Dirty, Inspecting) dành riêng cho bộ phận Housekeeping.

---

## 🏗️ Kiến Trúc Hệ Thống (Architecture)

```text
┌────────────────────────────────────────────────────────────────────────┐
│                              CLIENT                                    │
│             Web Browser (Angular 17 SPA, Tailwind CSS)                 │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP / REST APIs
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        DOCKER COMPOSE NETWORK                          │
│                                                                        │
│   ┌────────────────────┐                   ┌───────────────────────┐   │
│   │   Nginx Reverse    │ ──/api/v1/───►    │   Spring Boot 3 App   │   │
│   │     Proxy (80)     │                   │     Backend (8080)    │   │
│   └────────────────────┘                   └───────────┬───────────┘   │
│                                                        │               │
│               ┌───────────────────┬────────────────────┼───────────┐   │
│               ▼                   ▼                    ▼           ▼   │
│       ┌───────────────┐   ┌───────────────┐   ┌──────────────┐ ┌─────┐ │
│       │  MySQL 8 DB   │   │ Redis 7 Cache │   │ Ollama (LLM) │ │MoMo │ │
│       │  (Port 3306)  │   │  (Port 6379)  │   │ (Port 11434) │ │ API │ │
│       └───────────────┘   └───────────────┘   └──────────────┘ └─────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

| Hạng mục | Công nghệ |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3, Spring Data JPA, Spring Security, Spring AI, Hibernate, Lombok |
| **Frontend** | Angular 17 (Standalone Components, Signals), TypeScript, SCSS, Tailwind CSS |
| **Database & Cache**| MySQL 8.0, Redis 7 (Alpine) |
| **AI & LLM** | Ollama, Qwen 2.5 (3B), JSON Tool Calling / Function Calling |
| **Third-Party APIs**| MoMo Payment Gateway, Google OAuth2, Gmail SMTP |
| **DevOps & Cloud** | Docker, Docker Compose, Nginx, AWS EC2 |

---

## 💻 Hướng Dẫn Cài Đặt & Chạy Dự Án

### Yêu cầu tiên quyết
* [Docker](https://docs.docker.com/get-docker/) & [Docker Compose](https://docs.docker.com/compose/)
* [Git](https://git-scm.com/)

### Cách 1: Chạy toàn bộ hệ thống bằng Docker Compose (Khuyên dùng)

1. **Clone repository:**
   ```bash
   git clone https://github.com/LongPhung205/aura-resort.git
   cd aura-resort
   ```

2. **Khởi chạy hệ thống bằng Docker Compose:**
   ```bash
   docker compose up -d --build
   ```

3. **Tải model AI cho container Ollama:**
   ```bash
   docker exec -it hotel_booking_ollama ollama pull qwen2.5:3b
   ```

4. **Truy cập ứng dụng:**
   * Frontend: `http://localhost`
   * Backend API: `http://localhost:8080/api/v1`
   * Database: `localhost:3306`

---

### Cách 2: Chạy môi trường Local Development

#### 1. Khởi động Backend (Spring Boot):
```bash
cd backend/booking_hotel
./mvnw clean spring-boot:run
```
*(Cấu hình cơ sở dữ liệu và thông tin bí mật có thể đặt tại file `application-local.properties` được tự động nạp mà không bị đẩy lên Git).*

#### 2. Khởi động Frontend (Angular):
```bash
cd frontend
npm install
npm start
```
Ứng dụng sẽ chạy tại `http://localhost:4200`.

---

## 📁 Cấu Trúc Thư Mục (Project Structure)

```text
aura-resort/
├── backend/
│   └── booking_hotel/          # Source code Spring Boot 3
│       ├── src/main/java/      # Controllers, Services, Repositories, Entities, Config
│       └── src/main/resources/ # application.properties
├── frontend/                   # Source code Angular 17
│   ├── src/app/
│   │   ├── core/               # Interceptors, Guards, Core Services
│   │   ├── features/           # Home, Villas, Booking, Account, Admin, Housekeeping
│   │   └── shared/             # AI Chat Widget, Header, Footer, Reusable UI
│   └── nginx.conf              # Cấu hình Nginx reverse proxy
├── database/
│   └── dump.sql                # File dữ liệu mẫu ban đầu
├── docker-compose.yml          # Đóng gói và điều phối 5 services
└── README.md                   # Tài liệu hướng dẫn dự án
```

---

## 👤 Tác Giả (Author)

* **Phùng Văn Long**
* **GitHub:** [@LongPhung205](https://github.com/LongPhung205)
* **Email:** phungvanlong10925@gmail.com
* **Project:** Aura Resort – Hotel & Villa Booking Platform
