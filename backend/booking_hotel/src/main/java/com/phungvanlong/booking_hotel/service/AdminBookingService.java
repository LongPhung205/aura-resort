package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.AdminBookingFilterRequest;
import com.phungvanlong.booking_hotel.dto.response.AdminBookingItemResponse;
import com.phungvanlong.booking_hotel.dto.response.BookingResponse;
import com.phungvanlong.booking_hotel.dto.response.GanttRoomAvailabilityResponse;
import com.phungvanlong.booking_hotel.dto.response.GanttVillaAvailabilityResponse;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

public interface AdminBookingService {
    PageResponse<AdminBookingItemResponse> getAdminBookings(AdminBookingFilterRequest filterRequest);
    BookingResponse checkInBooking(Long id);
    BookingResponse checkInBooking(Long id, String verifyCode);
    BookingResponse checkOutBooking(Long id);
    List<GanttVillaAvailabilityResponse> getGanttVillaAvailability(LocalDate startDate, int days);
    List<GanttRoomAvailabilityResponse> getGanttAvailability(LocalDate startDate, int days);
    BookingResponse createDirectBooking(com.phungvanlong.booking_hotel.dto.request.AdminDirectBookingRequest request);
}
