package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.AdminBookingFilterRequest;
import com.phungvanlong.booking_hotel.dto.request.BookingRequest;
import com.phungvanlong.booking_hotel.dto.response.AdminBookingItemResponse;
import com.phungvanlong.booking_hotel.dto.response.BookingResponse;
import com.phungvanlong.booking_hotel.dto.response.GanttRoomAvailabilityResponse;
import com.phungvanlong.booking_hotel.dto.response.GanttVillaAvailabilityResponse;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String userEmail);
    BookingResponse getBookingById(Long id, String userEmail);
    List<BookingResponse> getMyBookings(String userEmail);
    void cancelBooking(Long id, String userEmail);
    BookingResponse mockPaymentSuccess(Long id);
    void cancelExpiredBookings();
    BookingResponse checkInBooking(Long id);
    BookingResponse checkOutBooking(Long id);

    PageResponse<AdminBookingItemResponse> getAdminBookings(AdminBookingFilterRequest filterRequest);
    List<GanttVillaAvailabilityResponse> getGanttVillaAvailability(LocalDate startDate, int days);
    List<GanttRoomAvailabilityResponse> getGanttAvailability(LocalDate startDate, int days);
}
