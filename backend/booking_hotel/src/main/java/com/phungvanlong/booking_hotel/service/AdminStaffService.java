package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.BiometricCheckInRequest;
import com.phungvanlong.booking_hotel.dto.request.ShiftSwapActionRequest;
import com.phungvanlong.booking_hotel.dto.response.AttendanceLogResponse;
import com.phungvanlong.booking_hotel.dto.response.ShiftSwapResponse;
import com.phungvanlong.booking_hotel.dto.response.StaffRosterResponse;

import com.phungvanlong.booking_hotel.dto.request.ApproveWeeklyRegistrationRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateScheduleCellRequest;
import com.phungvanlong.booking_hotel.dto.request.WeeklyShiftRegistrationRequest;
import com.phungvanlong.booking_hotel.dto.response.WeeklyShiftRegistrationResponse;

import java.time.LocalDate;
import java.util.List;

public interface AdminStaffService {
    StaffRosterResponse getWeeklyRoster(LocalDate startDate);
    List<AttendanceLogResponse> getRecentAttendance();
    AttendanceLogResponse recordBiometricCheckIn(BiometricCheckInRequest request);
    List<ShiftSwapResponse> getSwapRequests();
    ShiftSwapResponse processSwapRequest(ShiftSwapActionRequest request, String approverEmail);

    void updateScheduleCell(UpdateScheduleCellRequest request);
    StaffRosterResponse generateAiWeeklyRoster(LocalDate startDate);

    WeeklyShiftRegistrationResponse submitWeeklyRegistration(Long staffId, WeeklyShiftRegistrationRequest request);
    WeeklyShiftRegistrationResponse getMyWeeklyRegistration(Long staffId, LocalDate weekStartDate);
    List<WeeklyShiftRegistrationResponse> getPendingWeeklyRegistrations();
    WeeklyShiftRegistrationResponse processWeeklyRegistration(ApproveWeeklyRegistrationRequest request, String approverEmail);
}
