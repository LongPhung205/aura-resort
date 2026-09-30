package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.AdminBookingFilterRequest;
import com.phungvanlong.booking_hotel.dto.response.*;
import com.phungvanlong.booking_hotel.service.BookingService;
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

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminBookingItemResponse>>> getAdminBookings(
            @ModelAttribute AdminBookingFilterRequest filterRequest) {
        PageResponse<AdminBookingItemResponse> result = bookingService.getAdminBookings(filterRequest);
        return ResponseEntity.ok(ApiResponse.success(result, "Lấy danh sách quản lý đặt phòng thành công"));
    }

    @GetMapping("/gantt")
    public ResponseEntity<ApiResponse<List<GanttRoomAvailabilityResponse>>> getGanttAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false, defaultValue = "7") int days) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        List<GanttRoomAvailabilityResponse> list = bookingService.getGanttAvailability(startDate, days);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy biểu đồ Gantt tình trạng phòng thành công"));
    }

    @GetMapping("/gantt-villas")
    public ResponseEntity<ApiResponse<List<GanttVillaAvailabilityResponse>>> getGanttVillaAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false, defaultValue = "7") int days) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        List<GanttVillaAvailabilityResponse> list = bookingService.getGanttVillaAvailability(startDate, days);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy biểu đồ Gantt tình trạng Villa thành công"));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<BookingResponse>> checkIn(@PathVariable Long id) {
        BookingResponse response = bookingService.checkInBooking(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Thực hiện Check-in thành công"));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<BookingResponse>> checkOut(@PathVariable Long id) {
        BookingResponse response = bookingService.checkOutBooking(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Thực hiện Check-out thành công"));
    }
}
