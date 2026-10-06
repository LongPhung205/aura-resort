# Client Account Portal Design Document

**Date:** 2026-10-04  
**Feature:** Client Account Portal (My Bookings, My Reviews, My Profile)  
**Status:** Approved by User  

---

## 1. Executive Summary
Build a unified, responsive, luxury client dashboard (`/account`) for resort guests. This portal allows customers to manage their bookings (status filtering, booking details modal, cancellation, review invocation), review history & management responses, and account profile / password settings.

---

## 2. Architecture & Routing

### 2.1 Route Definitions
All account views live under `/account`, protected by `authGuard`.
- `/account`: Master Account Portal component with sidebar and tabbed content views:
  - `tab=bookings`: "Chuyến đi của tôi" (My Bookings)
  - `tab=reviews`: "Đánh giá & Nhận xét" (My Reviews)
  - `tab=profile`: "Thông tin Tài khoản" (My Profile)

### 2.2 Navigation Integration
In `app/layout/main-layout/header/header.component.html`:
When a user is authenticated, the user profile dropdown provides quick links:
- `Chuyến đi của tôi` ➔ `/account?tab=bookings`
- `Đánh giá & Nhận xét` ➔ `/account?tab=reviews`
- `Thông tin tài khoản` ➔ `/account?tab=profile`
- `Cổng Quản Trị` (if Staff/Admin) ➔ `/admin`
- `Cổng Buồng Phòng` (if Housekeeping) ➔ `/housekeeping`
- `Đăng xuất` ➔ Clears auth token and redirects to `/login`.

---

## 3. Subsystem Breakdown

### 3.1 Subsystem 1: My Bookings ("Chuyến đi của tôi")
- **Metrics Bar:**
  - Total Bookings
  - Upcoming / Active Bookings
  - Completed Trips
- **Status Filters:**
  - `Tất cả` (All)
  - `Đang chờ` (PENDING)
  - `Sắp đến / Đang ở` (CONFIRMED, CHECKED_IN)
  - `Đã hoàn thành` (COMPLETED)
  - `Đã hủy` (CANCELLED)
- **Booking Card Elements:**
  - Villa thumbnail image, Villa Title, Zone Name, Room count.
  - Booking code (e.g. `#BK-20261004-98`).
  - Stay dates: Check-in ➔ Check-out with night count badge.
  - Guest counts (Adults / Children).
  - Status badge with color coding (Amber for PENDING, Emerald for CONFIRMED/CHECKED_IN, Sky for COMPLETED, Rose for CANCELLED).
  - Total pricing and payment method info.
  - Action buttons:
    - `Chi tiết & Hóa đơn`: Opens Booking Details Modal.
    - `Hủy đặt phòng`: Available for `PENDING` bookings; prompts confirmation modal before calling cancel API.
    - `Đánh giá ngay`: Available for `COMPLETED` bookings that haven't been reviewed yet.
- **Booking Detail Modal:**
  - Breakdown of base room price, extra services / combo packages, applied voucher discount, total paid.
  - Guest contact information and check-in instructions.
  - Print invoice / receipt action.

### 3.2 Subsystem 2: My Reviews ("Đánh giá & Nhận xét")
- **Tabs:**
  - **Đã đánh giá (Reviewed):**
    - List of past reviews authored by the logged-in customer.
    - Star rating (1-5★), review comment, timestamp.
    - Sentiment tag (Tích cực / Hài lòng / Cần cải thiện).
    - Management Response Card: Shows resort manager's reply text (`managementReply`) and response date (`repliedAt`).
  - **Chờ đánh giá (Pending Review):**
    - Displays completed bookings without reviews.
    - "Viết đánh giá" action button opens a modal to rate and submit comments.
- **Write Review Modal:**
  - Interactive 5-star rating selector.
  - Textarea for detailed guest feedback.
  - Submits to `POST /api/v1/reviews`.

### 3.3 Subsystem 3: My Profile ("Thông tin Tài khoản")
- **Profile Overview Card:**
  - User avatar with initial letters and active account status badge.
  - Email address (Read-only for security).
- **Personal Details Form:**
  - Full Name (`fullName`)
  - Phone Number (`phone`)
  - "Lưu thay đổi" button calling `PUT /api/v1/users/me`.
- **Change Password Card:**
  - Current Password
  - New Password
  - Confirm New Password
  - Client-side validation: Matching passwords and minimum 6 characters.
  - Submits to `PUT /api/v1/users/me/password`.

---

## 4. Backend API Specifications

### 4.1 Existing APIs
- `GET /api/v1/bookings`: Returns current user's bookings.
- `GET /api/v1/bookings/{id}`: Returns booking details.
- `POST /api/v1/bookings/{id}/cancel`: Cancels booking.
- `POST /api/v1/reviews`: Submits a review.

### 4.2 New / Extended APIs Needed
1. **User Profile Controller** (`UserController.java`):
   - `GET /api/v1/users/me`:
     - Returns `{ id, email, fullName, phone, role, provider, createdAt }`.
   - `PUT /api/v1/users/me`:
     - Accepts `{ fullName, phone }`, returns updated profile.
   - `PUT /api/v1/users/me/password`:
     - Accepts `{ currentPassword, newPassword }`, verifies old password and updates BCrypt hash.
2. **Review Controller Extension** (`ReviewController.java`):
   - `GET /api/v1/reviews/me`:
     - Returns all reviews submitted by the authenticated user with booking & villa details.

---

## 5. Security & Error Handling
- All endpoints use `Authentication authentication` to identify the logged-in user securely via JWT.
- In Angular, `authGuard` prevents unauthorized access to `/account`.
- Toast notifications give feedback on actions (profile saved, password changed, booking canceled, review posted).
- Skeleton / spinner loaders prevent UI jump during async HTTP calls.
