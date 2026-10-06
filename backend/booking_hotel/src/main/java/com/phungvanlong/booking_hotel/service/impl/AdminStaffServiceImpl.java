package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ApproveWeeklyRegistrationRequest;
import com.phungvanlong.booking_hotel.dto.request.BiometricCheckInRequest;
import com.phungvanlong.booking_hotel.dto.request.ShiftSwapActionRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateScheduleCellRequest;
import com.phungvanlong.booking_hotel.dto.request.WeeklyShiftRegistrationRequest;
import com.phungvanlong.booking_hotel.dto.response.AttendanceLogResponse;
import com.phungvanlong.booking_hotel.dto.response.ShiftSwapResponse;
import com.phungvanlong.booking_hotel.dto.response.StaffRosterResponse;
import com.phungvanlong.booking_hotel.dto.response.WeeklyShiftRegistrationResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.*;
import com.phungvanlong.booking_hotel.service.AdminStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStaffServiceImpl implements AdminStaffService {

    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final StaffScheduleRepository staffScheduleRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final ShiftSwapRequestRepository shiftSwapRequestRepository;
    private final WeeklyShiftRegistrationRepository weeklyShiftRegistrationRepository;

    @Override
    @Transactional
    public StaffRosterResponse getWeeklyRoster(LocalDate startDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        LocalDate endDate = startDate.plusDays(6);

        List<User> staffList = userRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.ROLE_CUSTOMER)
                .limit(20)
                .collect(Collectors.toList());

        List<StaffSchedule> existingSchedules = staffScheduleRepository.findByWorkDateBetweenOrderByWorkDateAsc(startDate, endDate);

        // If no schedules exist in DB for this date range, generate realistic initial schedules
        if (existingSchedules.isEmpty() && !staffList.isEmpty()) {
            existingSchedules = initDefaultSchedulesForRange(startDate, endDate, staffList);
        }

        // Map key: "staffId_workDate"
        Map<String, StaffSchedule> scheduleMap = new HashMap<>();
        for (StaffSchedule s : existingSchedules) {
            if (s.getStaff() != null && s.getWorkDate() != null) {
                scheduleMap.put(s.getStaff().getId() + "_" + s.getWorkDate(), s);
            }
        }

        String[] localAvatars = {
            "/assets/images/staff/avatar-nam.jpg",
            "/assets/images/staff/avatar-hoang.jpg",
            "/assets/images/staff/avatar-hoa.jpg",
            "/assets/images/staff/avatar-thao.jpg",
            "/assets/images/staff/avatar-minh.jpg",
            "/assets/images/staff/avatar-quang.jpg"
        };

        LocalDate today = LocalDate.now();
        int onDutyTodayCount = 0;
        int onLeaveTodayCount = 0;

        List<StaffRosterResponse.StaffWeeklyScheduleItem> memberSchedules = new ArrayList<>();
        int uIdx = 0;
        for (User u : staffList) {
            List<StaffRosterResponse.DayScheduleItem> days = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate d = startDate.plusDays(i);
                StaffSchedule sched = scheduleMap.get(u.getId() + "_" + d);

                String shiftName = "OFF Nghỉ";
                String shiftColor = "#64748b";
                String status = "OFF";

                if (sched != null) {
                    if (Boolean.TRUE.equals(sched.getIsOff()) || (sched.getShift() != null && sched.getShift().getName().contains("OFF"))) {
                        shiftName = "OFF Nghỉ";
                        shiftColor = "#64748b";
                        status = "OFF";
                    } else if (sched.getShift() != null) {
                        shiftName = sched.getShift().getName();
                        shiftColor = sched.getShift().getColorCode() != null ? sched.getShift().getColorCode() : "#10b981";
                        status = sched.getStatus() != null ? sched.getStatus() : "SCHEDULED";
                    }
                }

                if (d.equals(today)) {
                    if ("OFF".equalsIgnoreCase(status)) {
                        onLeaveTodayCount++;
                    } else {
                        onDutyTodayCount++;
                    }
                }

                days.add(StaffRosterResponse.DayScheduleItem.builder()
                        .date(d)
                        .shiftName(shiftName)
                        .shiftColor(shiftColor)
                        .status(status)
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
                    .avatarUrl(localAvatars[uIdx % localAvatars.length])
                    .days(days)
                    .build());
            uIdx++;
        }

        int totalStaff = staffList.size();

        return StaffRosterResponse.builder()
                .weekStartDate(startDate)
                .weekEndDate(endDate)
                .totalStaff(totalStaff)
                .onDutyToday(onDutyTodayCount)
                .lateToday(0)
                .onLeaveToday(onLeaveTodayCount)
                .staffMembers(memberSchedules)
                .build();
    }

    private List<StaffSchedule> initDefaultSchedulesForRange(LocalDate startDate, LocalDate endDate, List<User> staffList) {
        List<Shift> shifts = shiftRepository.findAll();
        if (shifts.isEmpty()) return Collections.emptyList();

        Shift morning = findShiftByKeyword(shifts, "Sáng");
        Shift afternoon = findShiftByKeyword(shifts, "Chiều");
        Shift night = findShiftByKeyword(shifts, "Đêm");
        Shift off = findShiftByKeyword(shifts, "OFF");

        Shift[] rotation = {morning, afternoon, night, off};
        List<StaffSchedule> toSave = new ArrayList<>();

        int uIdx = 0;
        for (User u : staffList) {
            int daysBetween = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
            for (int d = 0; d < daysBetween; d++) {
                LocalDate date = startDate.plusDays(d);
                int rotIdx = (uIdx + d) % 4;
                Shift s = rotation[rotIdx];
                boolean isOff = s.getName().contains("OFF");

                toSave.add(StaffSchedule.builder()
                        .staff(u)
                        .shift(s)
                        .workDate(date)
                        .isOff(isOff)
                        .status(isOff ? "OFF" : "SCHEDULED")
                        .note("Lịch tiêu chuẩn resort")
                        .build());
            }
            uIdx++;
        }
        return staffScheduleRepository.saveAll(toSave);
    }

    private Shift findShiftByKeyword(List<Shift> shifts, String kw) {
        return shifts.stream()
                .filter(s -> s.getName().toLowerCase().contains(kw.toLowerCase()))
                .findFirst()
                .orElse(shifts.get(0));
    }

    @Override
    @Transactional
    public void updateScheduleCell(UpdateScheduleCellRequest request) {
        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên ID: " + request.getStaffId()));

        StaffSchedule schedule = staffScheduleRepository.findByStaffIdAndWorkDate(request.getStaffId(), request.getWorkDate())
                .orElseGet(() -> StaffSchedule.builder()
                        .staff(staff)
                        .workDate(request.getWorkDate())
                        .build());

        List<Shift> shifts = shiftRepository.findAll();
        String type = request.getShiftType().toUpperCase();

        if ("OFF".equals(type)) {
            Shift offShift = shifts.stream().filter(s -> s.getName().contains("OFF")).findFirst().orElse(null);
            schedule.setShift(offShift);
            schedule.setIsOff(true);
            schedule.setStatus("OFF");
        } else {
            Shift matchedShift = null;
            if (type.contains("MORNING") || type.contains("SÁNG")) {
                matchedShift = findShiftByKeyword(shifts, "Sáng");
            } else if (type.contains("AFTERNOON") || type.contains("CHIỀU")) {
                matchedShift = findShiftByKeyword(shifts, "Chiều");
            } else if (type.contains("NIGHT") || type.contains("ĐÊM")) {
                matchedShift = findShiftByKeyword(shifts, "Đêm");
            } else if (type.contains("ONCALL") || type.contains("VIP")) {
                matchedShift = findShiftByKeyword(shifts, "On-Call");
            }
            if (matchedShift == null && !shifts.isEmpty()) {
                matchedShift = shifts.get(0);
            }
            schedule.setShift(matchedShift);
            schedule.setIsOff(false);
            schedule.setStatus("SCHEDULED");
        }

        if (request.getNote() != null) {
            schedule.setNote(request.getNote());
        }

        staffScheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public StaffRosterResponse generateAiWeeklyRoster(LocalDate startDate) {
        if (startDate == null) startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);

        List<User> staffList = userRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.ROLE_CUSTOMER)
                .toList();

        // Remove existing schedules for this week to rebuild cleanly
        staffScheduleRepository.deleteByWorkDateBetween(startDate, endDate);
        initDefaultSchedulesForRange(startDate, endDate, staffList);

        return getWeeklyRoster(startDate);
    }

    @Override
    @Transactional
    public WeeklyShiftRegistrationResponse submitWeeklyRegistration(Long staffId, WeeklyShiftRegistrationRequest request) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên ID: " + staffId));

        LocalDate weekStartDate = request.getWeekStartDate();
        LocalDate weekEndDate = weekStartDate.plusDays(6);

        WeeklyShiftRegistration reg = weeklyShiftRegistrationRepository.findByStaffIdAndWeekStartDate(staffId, weekStartDate)
                .orElseGet(() -> WeeklyShiftRegistration.builder()
                        .staff(staff)
                        .weekStartDate(weekStartDate)
                        .weekEndDate(weekEndDate)
                        .build());

        reg.setStatus("PENDING");
        reg.setPreferredZone(request.getPreferredZone());
        reg.setNotes(request.getNotes());
        reg.setApprover(null);
        reg.setApprovedAt(null);
        reg.setRejectionReason(null);

        // Update details
        if (reg.getDetails() == null) {
            reg.setDetails(new ArrayList<>());
        } else {
            reg.getDetails().clear();
        }

        if (request.getDays() != null) {
            int dayIndex = 0;
            for (WeeklyShiftRegistrationRequest.DayRegistrationItem item : request.getDays()) {
                LocalDate itemDate = item.getWorkDate();
                if (itemDate == null) {
                    itemDate = weekStartDate.plusDays(dayIndex);
                }
                WeeklyShiftRegistrationDetail detail = WeeklyShiftRegistrationDetail.builder()
                        .registration(reg)
                        .workDate(itemDate)
                        .dayOfWeek(item.getDayOfWeek() != null ? item.getDayOfWeek() : "T" + (dayIndex + 2))
                        .shiftType(item.getShiftType() != null ? item.getShiftType() : "MORNING")
                        .note(item.getNote())
                        .build();
                reg.getDetails().add(detail);
                dayIndex++;
            }
        }

        WeeklyShiftRegistration saved = weeklyShiftRegistrationRepository.save(reg);
        return WeeklyShiftRegistrationResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyShiftRegistrationResponse getMyWeeklyRegistration(Long staffId, LocalDate weekStartDate) {
        return weeklyShiftRegistrationRepository.findByStaffIdAndWeekStartDate(staffId, weekStartDate)
                .map(WeeklyShiftRegistrationResponse::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeeklyShiftRegistrationResponse> getPendingWeeklyRegistrations() {
        return weeklyShiftRegistrationRepository.findByStatusWithDetails("PENDING").stream()
                .map(WeeklyShiftRegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WeeklyShiftRegistrationResponse processWeeklyRegistration(ApproveWeeklyRegistrationRequest request, String approverEmail) {
        WeeklyShiftRegistration reg = weeklyShiftRegistrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đăng ký ca tuần ID: " + request.getRegistrationId()));

        User approver = approverEmail != null ? userRepository.findByEmail(approverEmail).orElse(null) : null;

        if (Boolean.TRUE.equals(request.getApproved())) {
            reg.setStatus("APPROVED");
            reg.setApprover(approver);
            reg.setApprovedAt(LocalDateTime.now());

            // AUTO-UPSERT TO LIVE StaffSchedule TABLE
            List<Shift> allShifts = shiftRepository.findAll();
            for (WeeklyShiftRegistrationDetail detail : reg.getDetails()) {
                StaffSchedule sched = staffScheduleRepository.findByStaffIdAndWorkDate(reg.getStaff().getId(), detail.getWorkDate())
                        .orElseGet(() -> StaffSchedule.builder()
                                .staff(reg.getStaff())
                                .workDate(detail.getWorkDate())
                                .build());

                String sType = detail.getShiftType() != null ? detail.getShiftType().toUpperCase() : "OFF";
                if ("OFF".equals(sType)) {
                    Shift off = allShifts.stream().filter(s -> s.getName().contains("OFF")).findFirst().orElse(null);
                    sched.setShift(off);
                    sched.setIsOff(true);
                    sched.setStatus("OFF");
                } else {
                    Shift shift = null;
                    if (sType.contains("MORNING")) shift = findShiftByKeyword(allShifts, "Sáng");
                    else if (sType.contains("AFTERNOON")) shift = findShiftByKeyword(allShifts, "Chiều");
                    else if (sType.contains("NIGHT")) shift = findShiftByKeyword(allShifts, "Đêm");
                    else if (sType.contains("ONCALL")) shift = findShiftByKeyword(allShifts, "On-Call");

                    if (shift == null && !allShifts.isEmpty()) shift = allShifts.get(0);

                    sched.setShift(shift);
                    sched.setIsOff(false);
                    sched.setStatus("SCHEDULED");
                }
                sched.setNote(detail.getNote() != null ? detail.getNote() : "Phê duyệt từ đơn đăng ký ca");
                staffScheduleRepository.save(sched);
            }
        } else {
            reg.setStatus("REJECTED");
            reg.setApprover(approver);
            reg.setApprovedAt(LocalDateTime.now());
            reg.setRejectionReason(request.getRejectionReason());
        }

        return WeeklyShiftRegistrationResponse.fromEntity(weeklyShiftRegistrationRepository.save(reg));
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
    @Transactional(readOnly = true)
    public List<ShiftSwapResponse> getSwapRequests() {
        return shiftSwapRequestRepository.findAllWithDetails().stream()
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
