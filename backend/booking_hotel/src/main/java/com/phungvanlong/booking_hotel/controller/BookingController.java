package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.BookingRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.BookingResponse;
import com.phungvanlong.booking_hotel.service.BookingService;
import com.phungvanlong.booking_hotel.service.AdminBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final AdminBookingService adminBookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        BookingResponse booking = bookingService.createBooking(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, "Tạo đơn đặt phòng thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        List<BookingResponse> bookings = bookingService.getMyBookings(userEmail);
        return ResponseEntity.ok(ApiResponse.success(bookings, "Lấy danh sách đơn hàng thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        BookingResponse booking = bookingService.getBookingById(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success(booking, "Lấy thông tin đơn hàng thành công"));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        bookingService.cancelBooking(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success(null, "Hủy đơn hàng thành công"));
    }

    @PostMapping("/{id}/mock-pay")
    public ResponseEntity<ApiResponse<BookingResponse>> mockPay(@PathVariable Long id) {
        BookingResponse booking = bookingService.mockPaymentSuccess(id);
        return ResponseEntity.ok(ApiResponse.success(booking, "Giả lập thanh toán thành công"));
    }

    @PostMapping("/{id}/check-in")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<BookingResponse>> checkIn(
            @PathVariable Long id,
            @RequestParam(required = false) String verifyCode) {
        BookingResponse booking = adminBookingService.checkInBooking(id, verifyCode);
        return ResponseEntity.ok(ApiResponse.success(booking, "Check-in thành công"));
    }

    @PostMapping("/{id}/check-out")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<BookingResponse>> checkOut(@PathVariable Long id) {
        BookingResponse booking = adminBookingService.checkOutBooking(id);
        return ResponseEntity.ok(ApiResponse.success(booking, "Check-out thành công"));
    }
}
