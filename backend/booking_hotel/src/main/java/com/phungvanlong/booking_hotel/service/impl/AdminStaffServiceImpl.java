package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.BiometricCheckInRequest;
import com.phungvanlong.booking_hotel.dto.request.ShiftSwapActionRequest;
import com.phungvanlong.booking_hotel.dto.response.AttendanceLogResponse;
import com.phungvanlong.booking_hotel.dto.response.ShiftSwapResponse;
import com.phungvanlong.booking_hotel.dto.response.StaffRosterResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.*;
import com.phungvanlong.booking_hotel.service.AdminStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStaffServiceImpl implements AdminStaffService {

    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final StaffScheduleRepository staffScheduleRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final ShiftSwapRequestRepository shiftSwapRequestRepository;

    @Override
    public StaffRosterResponse getWeeklyRoster(LocalDate startDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        LocalDate endDate = startDate.plusDays(6);

        List<User> staffList = userRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.ROLE_CUSTOMER)
                .limit(10)
                .collect(Collectors.toList());

        List<StaffRosterResponse.StaffWeeklyScheduleItem> memberSchedules = new ArrayList<>();
        String[] shiftNames = {"Ca Sáng", "Ca Chiều", "Ca Đêm", "Nghỉ"};
        String[] colors = {"#3b82f6", "#10b981", "#8b5cf6", "#6b7280"};

        String[] localAvatars = {
            "/assets/images/staff/avatar-nam.jpg",
            "/assets/images/staff/avatar-hoang.jpg",
            "/assets/images/staff/avatar-hoa.jpg",
            "/assets/images/staff/avatar-thao.jpg",
            "/assets/images/staff/avatar-minh.jpg",
            "/assets/images/staff/avatar-quang.jpg"
        };

        int idx = 0;
        for (User u : staffList) {
            List<StaffRosterResponse.DayScheduleItem> days = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate d = startDate.plusDays(i);
                int shiftIdx = (idx + i) % 4;
                days.add(StaffRosterResponse.DayScheduleItem.builder()
                        .date(d)
                        .shiftName(shiftNames[shiftIdx])
                        .shiftColor(colors[shiftIdx])
                        .status(shiftIdx == 3 ? "OFF" : "SCHEDULED")
                        .build());
            }

            String dept = u.getRole() == Role.ROLE_RECEPTIONIST ? "Lễ Tân Tiền Sảnh" :
                    u.getRole() == Role.ROLE_BUTLER ? "Quản Gia VIP" :
                            u.getRole() == Role.ROLE_HOUSEKEEPING ? "Buồng Phòng & Ozone" :
                                    u.getRole() == Role.ROLE_ACCOUNTANT ? "Kế Toán" : "Bộ Phận Vận Hành";

            memberSchedules.add(StaffRosterResponse.StaffWeeklyScheduleItem.builder()
                    .staffId(u.getId())
                    .fullName(u.getFullName())
                    .role(u.getRole().name())
                    .department(dept)
                    .avatarUrl(localAvatars[idx % localAvatars.length])
                    .days(days)
                    .build());
            idx++;
        }

        int totalStaff = staffList.size();
        int onDuty = totalStaff > 0 ? (int) Math.max(1, Math.round(totalStaff * 0.7)) : 0;
        int onLeave = Math.max(0, totalStaff - onDuty);

        return StaffRosterResponse.builder()
                .weekStartDate(startDate)
                .weekEndDate(endDate)
                .totalStaff(totalStaff)
                .onDutyToday(onDuty)
                .lateToday(0)
                .onLeaveToday(onLeave)
                .staffMembers(memberSchedules)
                .build();
    }

    @Override
    public List<AttendanceLogResponse> getRecentAttendance() {
        List<AttendanceLog> logs = attendanceLogRepository.findTop20ByOrderByCheckTimeDesc();
        return logs.stream().map(AttendanceLogResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttendanceLogResponse recordBiometricCheckIn(BiometricCheckInRequest request) {
        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên ID: " + request.getStaffId()));

        AttendanceLog log = AttendanceLog.builder()
                .staff(staff)
                .checkTime(LocalDateTime.now())
                .checkType(request.getCheckType() != null ? request.getCheckType() : "CHECK_IN")
                .method(request.getMethod() != null ? request.getMethod() : "FACE_ID")
                .locationName(request.getLocationName() != null ? request.getLocationName() : "Sảnh Chính Resort")
                .latitude(request.getLatitude() != null ? request.getLatitude() : 12.2388)
                .longitude(request.getLongitude() != null ? request.getLongitude() : 109.1967)
                .matchAccuracy(request.getMatchAccuracy() != null ? request.getMatchAccuracy() : 99.2)
                .photoUrl(request.getPhotoUrl())
                .status("VALID")
                .build();

        return AttendanceLogResponse.fromEntity(attendanceLogRepository.save(log));
    }

    @Override
    public List<ShiftSwapResponse> getSwapRequests() {
        return shiftSwapRequestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ShiftSwapResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShiftSwapResponse processSwapRequest(ShiftSwapActionRequest request, String approverEmail) {
        ShiftSwapRequest swap = shiftSwapRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đổi ca ID: " + request.getRequestId()));

        User approver = null;
        if (approverEmail != null) {
            approver = userRepository.findByEmail(approverEmail).orElse(null);
        }

        swap.setStatus(Boolean.TRUE.equals(request.getApproved()) ? "APPROVED" : "REJECTED");
        swap.setApprover(approver);
        swap.setActionAt(LocalDateTime.now());
        if (request.getRejectReason() != null) {
            swap.setReason(swap.getReason() + " [Lý do từ chối: " + request.getRejectReason() + "]");
        }

        return ShiftSwapResponse.fromEntity(shiftSwapRequestRepository.save(swap));
    }
}
