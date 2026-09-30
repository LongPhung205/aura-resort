---
trigger: always_on
glob:
description: "Ponytail & Coding Conventions for Java/Spring Boot."
---

# Ponytail Rules (Java / Spring Boot Edition)

Trước khi viết bất kỳ dòng code nào, hãy áp dụng tư duy "lười biếng" của senior dev. Hãy dừng lại ở bước đầu tiên đúng với tình huống của bạn:

1. **Có thực sự cần thiết không?** → Không: bỏ qua (YAGNI). Đừng tạo các DTO, Service, hay Controller nếu nghiệp vụ chưa yêu cầu.
2. **Đã có sẵn trong project chưa?** → Tái sử dụng, đừng viết lại (VD: kế thừa `BaseEntity`, dùng chung các lớp Custom Exception, Utility class).
3. **Java JDK có hỗ trợ không?** → Dùng luôn. Tận dụng triệt để `java.time`, Java Streams, Optional thay vì viết vòng lặp rườm rà.
4. **Spring/Hibernate/Lombok làm được không?** → Hãy để framework lo. Dùng Lombok (`@Getter`, `@Setter`, `@Builder`) để giảm boilerplate code. Dùng Spring Data JPA derived queries (`findBy...`) thay vì tự viết câu lệnh JPQL dài dòng.
5. **Thư viện đã cài đặt có sẵn không?** → Tận dụng các thư viện như Apache Commons, MapStruct, Jackson có sẵn trong project thay vì tự implement.
6. **Có thể viết ngắn gọn không?** → Hãy viết ngắn gọn. Dùng lambda expressions, method references.
7. **Chỉ khi không thuộc các trường hợp trên:** Mới viết giải pháp code tự custom tối giản nhất có thể chạy được.

### Nguyên tắc cốt lõi

- **Đọc trước khi làm**: Nguyên tắc này áp dụng *sau khi* bạn đã hiểu bài toán. Hãy đọc kỹ các Entity liên quan, hiểu rõ luồng của Service hiện tại trước khi quyết định viết thêm code. Hãy "lười" trong việc viết code mới, nhưng đừng "lười" đọc code cũ.
- **Lười, nhưng không ẩu**: Tuyệt đối không bao giờ được bỏ qua các yếu tố sau:
  - **Data Validation & Security**: Luôn dùng `@Valid`, `jakarta.validation`.
  - **Transaction Management**: Nhớ dùng `@Transactional` ở Service layer để tránh lỗi dữ liệu.
  - **Hiệu suất DB**: Phải xử lý vấn đề N+1 query (dùng `@EntityGraph` hoặc `JOIN FETCH`), không bao giờ để code sinh ra hàng tá câu query dư thừa.

# Coding Conventions & Triển khai chung

Để đảm bảo tính nhất quán trong dự án, toàn bộ code cần tuân theo các tiêu chuẩn sau:

### 1. Quy tắc đặt tên (Naming Conventions)
- **Packages**: Chữ thường (`lowercase`), không dùng dấu gạch dưới (VD: `com.phungvanlong.booking_hotel.service`).
- **Classes / Interfaces / Records**: Viết hoa chữ cái đầu mỗi từ (`PascalCase`) (VD: `UserService`, `DiscountType`).
- **Methods / Variables**: Viết thường chữ cái đầu, các từ sau viết hoa chữ cái đầu (`camelCase`). Cần đặt tên rõ nghĩa, không viết tắt tùy tiện (VD: `calculateTotalAmount()`, `customerName` thay vì `cn`).
- **Constants (Hằng số)**: Viết hoa toàn bộ, phân cách bằng dấu gạch dưới (`UPPER_SNAKE_CASE`) (VD: `MAX_RETRY_COUNT`).
- **Enums**: Tên enum dùng `PascalCase`, các phần tử bên trong dùng `UPPER_SNAKE_CASE` (VD: `PERCENTAGE`, `FIXED_AMOUNT`).

### 2. Tiêu chuẩn Kiến trúc Phân tầng (Layered Architecture)
- **Controller Layer (`@RestController`)**:
  - Chỉ làm nhiệm vụ tiếp nhận HTTP Request, gọi Service và trả về HTTP Response.
  - Tuyệt đối **không chứa Business Logic** ở đây.
  - Trả về DTO (Data Transfer Object), **không trả trực tiếp Entity** ra ngoài API (để tránh rò rỉ dữ liệu DB, lặp vô hạn JSON...).
- **Service Layer (`@Service`)**:
  - Chứa toàn bộ Core Business Logic.
  - Luôn sử dụng interface cho Service (`UserService`) và implement ở class riêng (`UserServiceImpl`) nếu dự án yêu cầu, hoặc class trực tiếp tùy convention của team.
- **Repository Layer (`@Repository`)**:
  - Nơi duy nhất giao tiếp với Database. Trả về Optional<> thay vì object null nếu tra cứu 1 dòng.
- **DTOs & Mappers**:
  - Tách biệt rõ ràng request DTO (dữ liệu vào) và response DTO (dữ liệu ra).
  - Khuyến khích dùng `MapStruct` hoặc `ModelMapper` để map giữa Entity và DTO.

### 3. Quy chuẩn thiết kế RESTful API
- **Endpoint**: Dùng danh từ số nhiều (VD: `/api/v1/users`, `/api/v1/rooms`). Không dùng động từ trong endpoint (sai: `/api/v1/getUsers`).
- **HTTP Methods**: 
  - `GET`: Lấy dữ liệu.
  - `POST`: Tạo mới.
  - `PUT`: Cập nhật toàn bộ object.
  - `PATCH`: Cập nhật 1 phần (VD: đổi mật khẩu).
  - `DELETE`: Xóa.
- **Response Format**: Cố gắng chuẩn hóa kiểu Response trả về, ví dụ có 1 class `ApiResponse<T>` chung bọc lại dữ liệu (chứa `status`, `message`, `data`).

### 4. Xử lý Lỗi (Exception Handling)
- Dùng **Global Exception Handler** (`@RestControllerAdvice`) để bắt và xử lý lỗi tập trung toàn hệ thống. Không viết `try-catch` vụn vặt ở khắp các Controller.
- Quăng (throw) **Custom Exception** (như `ResourceNotFoundException`, `BusinessException`) từ tầng Service để Controller/Advice xử lý và map sang HTTP Status phù hợp (VD: 404 Not Found, 400 Bad Request).

