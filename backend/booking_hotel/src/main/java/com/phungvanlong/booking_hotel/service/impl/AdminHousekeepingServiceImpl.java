package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.AssignHousekeepingTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.HousekeepingChecklistRequest;
import com.phungvanlong.booking_hotel.dto.request.UpdateCleaningProgressRequest;
import com.phungvanlong.booking_hotel.dto.response.HousekeeperSummaryDto;
import com.phungvanlong.booking_hotel.dto.response.HousekeepingTaskResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.dto.request.CreateRefillTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.RefillItemCheckRequest;
import com.phungvanlong.booking_hotel.service.AdminRefillService;
import com.phungvanlong.booking_hotel.service.NotificationService;
import com.phungvanlong.booking_hotel.repository.HousekeepingTaskRepository;
import com.phungvanlong.booking_hotel.repository.RoomRepository;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.service.AdminHousekeepingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminHousekeepingServiceImpl implements AdminHousekeepingService {

    private final HousekeepingTaskRepository housekeepingTaskRepository;
    private final VillaRepository villaRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AdminRefillService adminRefillService;
    private final NotificationService notificationService;

    @Override
    public List<HousekeepingTaskResponse> getAllTasks(String status) {
        List<HousekeepingTask> tasks;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            tasks = housekeepingTaskRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            tasks = housekeepingTaskRepository.findAll();
        }
        return tasks.stream().map(HousekeepingTaskResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<HousekeepingTaskResponse> getMyTasks(String housekeeperEmail, Long housekeeperId) {
        List<HousekeepingTask> tasks;
        if (housekeeperId != null) {
            tasks = housekeepingTaskRepository.findByHousekeeperIdOrderByCreatedAtDesc(housekeeperId);
        } else if (housekeeperEmail != null && !housekeeperEmail.isBlank()) {
            User user = userRepository.findByEmail(housekeeperEmail).orElse(null);
            if (user != null && user.getRole() == Role.ROLE_ADMIN) {
                tasks = housekeepingTaskRepository.findAll();
            } else {
                tasks = housekeepingTaskRepository.findByHousekeeperEmailOrderByCreatedAtDesc(housekeeperEmail);
            }
        } else {
            tasks = housekeepingTaskRepository.findAll();
        }
        return tasks.stream().map(HousekeepingTaskResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<HousekeeperSummaryDto> getAvailableHousekeepers() {
        List<User> housekeepers = userRepository.findByRole(Role.ROLE_HOUSEKEEPING);
        return housekeepers.stream().map(u -> {
            List<HousekeepingTask> active = housekeepingTaskRepository.findByHousekeeperIdOrderByCreatedAtDesc(u.getId()).stream()
                    .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());
            return HousekeeperSummaryDto.builder()
                    .id(u.getId())
                    .fullName(u.getFullName())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .activeTasksCount(active.size())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse assignTask(AssignHousekeepingTaskRequest request, String supervisorEmail) {
        HousekeepingTask task;
        Villa villa = null;
        Room room = null;

        if (request.getTaskId() != null) {
            task = housekeepingTaskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + request.getTaskId()));
            villa = task.getVilla();
            room = task.getRoom();
        } else {
            Long targetId = request.getRoomId() != null ? request.getRoomId() : request.getVillaId();
            if (targetId == null) {
                throw new ResourceNotFoundException("Vui lòng cung cấp taskId, roomId hoặc villaId");
            }
            villa = villaRepository.findById(targetId).orElse(null);

            if (villa == null) {
                room = roomRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Villa hoặc Phòng ID: " + targetId));
                villa = room.getVilla();
            }

            if (villa != null) {
                task = housekeepingTaskRepository.findFirstByVillaIdAndStatusIn(
                        villa.getId(), List.of("PENDING", "IN_PROGRESS", "OZONE_RUNNING", "INSPECTED"))
                        .orElse(HousekeepingTask.builder().villa(villa).build());
            } else {
                task = housekeepingTaskRepository.findFirstByRoomIdAndStatusIn(
                        room.getId(), List.of("PENDING", "IN_PROGRESS", "OZONE_RUNNING", "INSPECTED"))
                        .orElse(HousekeepingTask.builder().room(room).build());
            }
        }

        if (villa != null) {
            task.setVilla(villa);
            villa.setStatus(VillaStatus.CLEANING);
            villa.setOzoneStatus("CLEANING");
            villaRepository.save(villa);
        }

        if (room != null) {
            task.setRoom(room);
            room.setStatus(RoomStatus.CLEANING);
            room.setOzoneStatus("CLEANING");
            roomRepository.save(room);
        }

        User housekeeper = userRepository.findById(request.getHousekeeperId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên buồng phòng ID: " + request.getHousekeeperId()));

        User supervisor = null;
        if (supervisorEmail != null && !supervisorEmail.isBlank()) {
            supervisor = userRepository.findByEmail(supervisorEmail).orElse(null);
        }

        task.setHousekeeper(housekeeper);
        if (supervisor != null) {
            task.setSupervisor(supervisor);
        }
        if (request.getTaskType() != null && !request.getTaskType().isBlank()) {
            task.setTaskType(request.getTaskType());
        }
        if (task.getStatus() == null || "PENDING".equals(task.getStatus())) {
            task.setStatus("PENDING");
        }
        if (request.getNotes() != null) {
            task.setSupervisorNote(request.getNotes());
        }

        HousekeepingTask saved = housekeepingTaskRepository.save(task);
        log.info("Supervisor assigned task {} to housekeeper {}", saved.getId(), housekeeper.getFullName());
        return HousekeepingTaskResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse startCleaning(Long taskId, String housekeeperEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        LocalDateTime now = LocalDateTime.now();
        task.setStatus("IN_PROGRESS");
        task.setStartedAt(now);
        task.setOzoneStartedAt(now);
        task.setOzoneEndedAt(now.plusMinutes(45));

        Villa villa = task.getVilla();
        if (villa != null) {
            villa.setStatus(VillaStatus.CLEANING);
            villa.setOzoneStatus("CLEANING");
            villaRepository.save(villa);
        }

        Room room = task.getRoom();
        if (room != null) {
            room.setStatus(RoomStatus.CLEANING);
            room.setOzoneStatus("CLEANING");
            roomRepository.save(room);
        }

        HousekeepingTask saved = housekeepingTaskRepository.save(task);
        return HousekeepingTaskResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse updateProgress(UpdateCleaningProgressRequest request, String housekeeperEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + request.getTaskId()));

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            task.setStatus(request.getStatus());
        }
        if (request.getChecklistJson() != null) {
            task.setChecklistJson(request.getChecklistJson());
        }
        if (request.getCleaningNote() != null) {
            task.setCleaningNote(request.getCleaningNote());
        }
        if (request.getEvidencePhotoUrl() != null) {
            task.setEvidencePhotoUrl(request.getEvidencePhotoUrl());
        }

        HousekeepingTask saved = housekeepingTaskRepository.save(task);
        return HousekeepingTaskResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse completeCleaning(Long taskId, String cleaningNote, String housekeeperEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        task.setStatus("INSPECTED");
        task.setCompletedAt(LocalDateTime.now());
        if (cleaningNote != null && !cleaningNote.isBlank()) {
            task.setCleaningNote(cleaningNote);
        }

        HousekeepingTask saved = housekeepingTaskRepository.save(task);
        return HousekeepingTaskResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse approveTask(Long taskId, String supervisorEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        if (supervisorEmail != null && !supervisorEmail.isBlank()) {
            User supervisor = userRepository.findByEmail(supervisorEmail).orElse(null);
            if (supervisor != null) {
                task.setSupervisor(supervisor);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        task.setStatus("COMPLETED");
        task.setCompletedAt(now);

        Villa villa = task.getVilla();
        if (villa != null) {
            villa.setStatus(VillaStatus.AVAILABLE);
            villa.setOzoneStatus("CLEANED");
            villa.setLastCleanedAt(now);
            villa.setCurrentGuestName(null);
            villaRepository.save(villa);
        }

        Room room = task.getRoom();
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE);
            room.setOzoneStatus("CLEANED");
            room.setLastCleanedAt(now);
            room.setCurrentGuestName(null);
            roomRepository.save(room);
        }

        HousekeepingTask saved = housekeepingTaskRepository.save(task);

        if ("CHECKOUT_DEEP".equals(saved.getTaskType()) && villa != null) {
            try {
                CreateRefillTaskRequest refillReq = CreateRefillTaskRequest.builder()
                        .villaId(villa.getId())
                        .housekeepingTaskId(saved.getId())
                        .note("Tự động bù đồ sau khi hoàn tất dọn phòng Checkout")
                        .items(new ArrayList<>())
                        .build();
                adminRefillService.createRefillTask(refillReq, "Hệ thống");
                log.info("Successfully created automatic RefillTask for villa {}", villa.getId());
            } catch (Exception ex) {
                log.warn("Could not create automatic refill task: {}", ex.getMessage());
            }
        }

        notificationService.sendNotification("REFRESH_GANTT");

        return HousekeepingTaskResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse startOzoneSterilisation(Long taskId) {
        return startCleaning(taskId, null);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse submitChecklist(HousekeepingChecklistRequest request) {
        HousekeepingTask task = housekeepingTaskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + request.getTaskId()));

        task.setChecklistJson(request.getChecklistJson());
        if (request.getEvidencePhotoUrl() != null) {
            task.setEvidencePhotoUrl(request.getEvidencePhotoUrl());
        }
        if (request.getNotes() != null) {
            task.setCleaningNote(request.getNotes());
        }
        task.setStatus("INSPECTED");

        return HousekeepingTaskResponse.fromEntity(housekeepingTaskRepository.save(task));
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse rejectTask(Long taskId, String reason, String supervisorEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        if (supervisorEmail != null && !supervisorEmail.isBlank()) {
            User supervisor = userRepository.findByEmail(supervisorEmail).orElse(null);
            if (supervisor != null) {
                task.setSupervisor(supervisor);
            }
        }

        task.setStatus("RE_CLEAN");
        task.setReCleanReason(reason);
        task.setSupervisorNote(reason);
        return HousekeepingTaskResponse.fromEntity(housekeepingTaskRepository.save(task));
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse claimTask(Long taskId, String staffEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        if (staffEmail != null && !staffEmail.isBlank()) {
            User housekeeper = userRepository.findByEmail(staffEmail).orElse(null);
            if (housekeeper != null) {
                task.setHousekeeper(housekeeper);
            }
        }

        task.setStatus("IN_PROGRESS");
        if (task.getStartedAt() == null) {
            task.setStartedAt(LocalDateTime.now());
        }
        return HousekeepingTaskResponse.fromEntity(housekeepingTaskRepository.save(task));
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse toggleOzone(Long taskId, Boolean enabled) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        task.setOzoneEnabled(enabled);
        if (Boolean.TRUE.equals(enabled)) {
            task.setOzoneStartedAt(LocalDateTime.now());
            task.setOzoneEndedAt(LocalDateTime.now().plusMinutes(20));
        } else {
            task.setOzoneStartedAt(null);
            task.setOzoneEndedAt(null);
        }
        return HousekeepingTaskResponse.fromEntity(housekeepingTaskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousekeepingTaskResponse> getAvailableDirtyRooms(String staffEmail) {
        // Lấy danh sách nhiệm vụ chưa ai nhận hoặc đang chờ dọn
        return housekeepingTaskRepository.findAll().stream()
                .filter(t -> "PENDING".equalsIgnoreCase(t.getStatus()) || t.getHousekeeper() == null)
                .map(HousekeepingTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse submitQc(Long taskId, String cleaningNote, String staffEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        task.setStatus("WAITING_QC");
        task.setCompletedAt(LocalDateTime.now());
        if (cleaningNote != null && !cleaningNote.isBlank()) {
            task.setCleaningNote(cleaningNote);
        }
        return HousekeepingTaskResponse.fromEntity(housekeepingTaskRepository.save(task));
    }


    @Override
    @Transactional
    public void clearAllData() {
        housekeepingTaskRepository.deleteAll();
    }
}
