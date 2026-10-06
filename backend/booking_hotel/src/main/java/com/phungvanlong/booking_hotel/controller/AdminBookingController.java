package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.AdminBookingFilterRequest;
import com.phungvanlong.booking_hotel.dto.response.*;
import com.phungvanlong.booking_hotel.service.AdminBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminBookingItemResponse>>> getAdminBookings(
            @ModelAttribute AdminBookingFilterRequest filterRequest) {
        PageResponse<AdminBookingItemResponse> result = adminBookingService.getAdminBookings(filterRequest);
        return ResponseEntity.ok(ApiResponse.success(result, "Lấy danh sách quản lý đặt phòng thành công"));
    }

    @GetMapping("/gantt")
    public ResponseEntity<ApiResponse<List<GanttRoomAvailabilityResponse>>> getGanttAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false, defaultValue = "7") int days) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        List<GanttRoomAvailabilityResponse> list = adminBookingService.getGanttAvailability(startDate, days);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy biểu đồ Gantt tình trạng phòng thành công"));
    }

    @GetMapping("/gantt-villas")
    public ResponseEntity<ApiResponse<List<GanttVillaAvailabilityResponse>>> getGanttVillaAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false, defaultValue = "7") int days) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        List<GanttVillaAvailabilityResponse> list = adminBookingService.getGanttVillaAvailability(startDate, days);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy biểu đồ Gantt tình trạng Villa thành công"));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<BookingResponse>> checkIn(
            @PathVariable Long id,
            @RequestParam(required = false) String verifyCode) {
        BookingResponse response = adminBookingService.checkInBooking(id, verifyCode);
        return ResponseEntity.ok(ApiResponse.success(response, "Thực hiện Check-in thành công"));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<BookingResponse>> checkOut(@PathVariable Long id) {
        BookingResponse response = adminBookingService.checkOutBooking(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Thực hiện Check-out thành công"));
    }

    @PostMapping("/direct")
    public ResponseEntity<ApiResponse<BookingResponse>> createDirectBooking(
            @RequestBody com.phungvanlong.booking_hotel.dto.request.AdminDirectBookingRequest request) {
        BookingResponse response = adminBookingService.createDirectBooking(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tạo đơn đặt phòng trực tiếp thành công"));
    }
}
