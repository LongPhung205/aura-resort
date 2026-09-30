---
name: thietke
description: Sử dụng khi thiết kế giao diện (UI) hoặc component cho trang web đặt phòng khách sạn, đặc biệt để tránh các mẫu thiết kế rập khuôn mặc định và tạo ra một bản sắc trực quan sang trọng, độc đáo.
---

# Kỹ Năng Thiết Kế Giao Diện Đặt Phòng Khách Sạn (Hotel Booking Design)

## Tổng Quan
Hãy tiếp cận việc thiết kế trang web đặt phòng khách sạn này như một giám đốc thiết kế (design lead) tại một studio danh tiếng. Khách hàng muốn một bản sắc trực quan độc đáo, sang trọng và không bị nhầm lẫn với bất kỳ ai. Tránh xa các thiết kế rập khuôn mang cảm giác "được tạo bởi AI" hay "mẫu có sẵn". Hãy đưa ra các lựa chọn có chủ đích về bảng màu, nghệ thuật chữ (typography), và bố cục dành riêng cho lĩnh vực khách sạn.

## Khi Nào Cần Sử Dụng
- Thiết kế trang chủ, danh sách phòng, chi tiết phòng, hoặc quy trình thanh toán.
- Khi cần định hình phong cách hình ảnh, màu sắc, font chữ cho website.
- Khi giao diện hiện tại trông quá chung chung, giống các template SaaS hoặc thiếu sự sang trọng.

## Các Nguyên Tắc Cốt Lõi (Core Principles)

### 1. Thể Hiện Rõ Ngành Dịch Vụ Khách Sạn
- Hero section là ấn tượng đầu tiên. Hãy mở đầu bằng thứ đặc trưng nhất của khách sạn: một bức ảnh không gian phòng tuyệt đẹp, một video chân thực, hoặc thanh tìm kiếm phòng (Search bar) trực quan. 
- Đừng dùng cấu trúc mặc định "Một con số lớn + nhãn nhỏ + màu nhấn gradient". Nó không phù hợp với trải nghiệm lưu trú.

### 2. Nghệ Thuật Chữ (Typography) Sang Trọng
- Chữ viết mang lại "tính cách" cho trang web. Chỉ cần dùng 1 hoặc 2 họ font chữ. Nếu dùng 2, hãy làm cho chúng khác biệt rõ ràng (ví dụ: Serif cho tiêu đề và Sans-serif cho nội dung).
- Tránh các phương pháp xử lý mặc định khiến giao diện trông giống AI tạo ra:
  - ❌ Nhấn mạnh chỉ một từ trong tiêu đề (in nghiêng, in đậm, hoặc khác màu một chữ duy nhất).
  - ❌ Lạm dụng IN HOA (ALL CAPS) cho các nhãn dán.
  - ❌ Thêm các nhãn (labels) văn bản không cần thiết phía trên nội dung.
- Giới hạn độ dài dòng chữ dưới 80 ký tự để dễ đọc.

### 3. Cấu Trúc Trực Quan & Chuyển Động
- Cấu trúc (viền, số thứ tự, nhãn, v.v.) dùng để truyền tải thông tin, không phải để trang trí. Đừng dùng đánh số (01 / 02 / 03) trừ khi nội dung thực sự là một quy trình theo trình tự (như các bước đặt phòng).
- **Chuyển động (Motion):** Tránh các hiệu ứng mặc định kiểu "thẻ nào cũng mờ dần và trượt lên (fade-and-slide-up)" hay "thẻ nào cũng có hiệu ứng hover". Chỉ dùng chuyển động một cách có chủ đích để thu hút sự chú ý hoặc khi phản hồi hành động của người dùng (ví dụ: mở form, xác nhận đặt phòng thành công).

## Các Dấu Hiệu Nhận Biết "Giao Diện AI Rập Khuôn" (Red Flags)

Giao diện do AI thiết kế thường mắc phải các lỗi mặc định sau đây. Bạn **TUYỆT ĐỐI TRÁNH** sử dụng chúng cho dự án khách sạn này trừ khi có lý do cực kỳ chính đáng:
1. Nền kem ấm (`#F4F1EA`) kết hợp với tiêu đề Serif và màu nhấn cam đất (đất nung) - đây là mặc định của nhiều AI.
2. Nền đen gần như tuyệt đối với một màu nhấn xanh acid hoặc đỏ tươi (vermilion) duy nhất.
3. Bộ thẻ SaaS (SaaS-card kit): Nội dung bị cắt thành các thẻ bo tròn giống hệt nhau, dùng chung một bán kính bo góc (border-radius) cho mọi thứ bất kể cấp độ, dùng chung một bóng mờ xám (`rgba(0,0,0,.1)`).
4. Các chi tiết trang trí rập khuôn: Nhãn dán IN HOA cách chữ (`T R A C K E D O U T`) trên mọi tiêu đề; nối chữ bằng dấu chấm giữa (`A · B · C`); font chữ Monospace cho các nhãn dữ liệu nhỏ; và luôn nối mũi tên (`→`) vào link hoặc nút bấm.

| Lời bào chữa thường gặp | Thực tế |
|---|---|
| "Tôi dùng bộ thẻ SaaS card cho nhanh." | Khách sạn bán trải nghiệm, không bán phần mềm. Thẻ rập khuôn làm mất đi sự sang trọng. |
| "Thêm mũi tên (→) vào nút bấm để người dùng chú ý." | Nút CTA tốt tự nó đã đủ thu hút bằng màu sắc và kích thước. |
| "Animation mọi thứ từ dưới lên cho sinh động." | Hiệu ứng tràn lan làm trang web trông rẻ tiền và rối mắt. |

## Bảng Đối Chiếu Nhanh (Quick Reference)

| Yếu tố | Hướng đi đúng (Do) | Hướng đi sai (Don't) |
|---|---|---|
| **Nút CTA** | "Kiểm tra phòng trống", màu nhấn độc nhất | "Submit", trùng màu với các nút phụ |
| **Hình ảnh** | To, sắc nét, có tính gợi mở | Nhỏ, tỷ lệ méo, bị che khuất bởi màu overlay đen đặc |
| **Form tìm kiếm** | Nổi bật phần Hero, dễ tương tác | Quá nhiều trường thông tin rườm rà |
| **Khoảng cách** | Rộng rãi, thoáng đãng (White-space) | Nhồi nhét thông tin (dense layout kiểu báo chí) |

## Quy Trình Áp Dụng (Workflow)

**Bước 1: Lập kế hoạch thiết kế (Design Plan)**
Trước khi viết code, hãy nghĩ ra một hệ thống token thiết kế (design token) ngắn gọn:
- **Màu sắc:** Xác định 4-6 mã hex cốt lõi mang cảm giác khách sạn (sang trọng, thư giãn, sạch sẽ).
- **Typography:** Định rõ vai trò của các font chữ.
- **Bố cục (Layout):** Quyết định cách căn chỉnh (trái, giữa) và cấu trúc. Cung cấp không gian "thở" (white-space) hào phóng.

**Bước 2: Tự phản biện (Critique)**
So sánh kế hoạch thiết kế của bạn với các dấu hiệu "rập khuôn" ở trên. Nếu nó giống những gì bạn thường làm cho một trang web bất kỳ, hãy sửa lại. 

**Bước 3: Lời Văn Tương Tác (Copywriting)**
- Giữ giọng văn lịch sự, chào đón như một lễ tân khách sạn.
- Sử dụng câu chủ động.
- Báo lỗi rõ ràng: Nếu người dùng chọn sai ngày, đừng xin lỗi vòng vo, hãy chỉ cách sửa: "Ngày trả phòng phải sau ngày nhận phòng."
