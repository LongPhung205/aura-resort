package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.*;
import com.phungvanlong.booking_hotel.dto.response.*;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.*;
import com.phungvanlong.booking_hotel.service.AdminRefillService;
import com.phungvanlong.booking_hotel.service.HousekeepingInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HousekeepingInspectionServiceImpl implements HousekeepingInspectionService {

    private final HousekeepingTaskRepository housekeepingTaskRepository;
    private final RoomConsumptionRecordRepository consumptionRecordRepository;
    private final LostAndFoundItemRepository lostAndFoundItemRepository;
    private final MaintenanceTicketRepository maintenanceTicketRepository;
    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final RoomRepository roomRepository;
    private final AdminRefillService adminRefillService;

    @Override
    @Transactional
    public List<RoomConsumptionResponse> submitInspection(Long taskId, SubmitRoomInspectionRequest request, String staffEmail) {
        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ dọn phòng ID: " + taskId));

        List<RoomConsumptionRecord> createdRecords = new ArrayList<>();
        List<RefillItemCheckRequest> refillItems = new ArrayList<>();

        // 1. Xử lý Minibar có tính phí
        if (request.getMinibarItems() != null) {
            for (SubmitRoomInspectionRequest.MinibarItemInspectionDto mini : request.getMinibarItems()) {
                int std = mini.getStandardQuantity() != null ? mini.getStandardQuantity() : 0;
                int cur = mini.getCurrentQuantity() != null ? mini.getCurrentQuantity() : 0;
                int consumed = Math.max(0, std - cur);

                BigDecimal unitPrice = mini.getUnitPrice() != null ? mini.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(consumed));

                if (consumed > 0) {
                    RoomConsumptionRecord record = RoomConsumptionRecord.builder()
                            .housekeepingTask(task)
                            .booking(task.getBooking())
                            .villa(task.getVilla())
                            .room(task.getRoom())
                            .itemType(RoomConsumptionItemType.MINIBAR_CONSUMED)
                            .itemName(mini.getItemName() != null ? mini.getItemName() : "Đồ minibar")
                            .quantity(consumed)
                            .unitPrice(unitPrice)
                            .totalPrice(totalPrice)
                            .status(RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL)
                            .recordedBy(staffEmail)
                            .build();

                    createdRecords.add(consumptionRecordRepository.save(record));
                }

                if (mini.getInventoryItemId() != null && consumed > 0) {
                    refillItems.add(RefillItemCheckRequest.builder()
                            .itemId(mini.getInventoryItemId())
                            .actualQuantity(cur)
                            .consumedQuantity(consumed)
                            .damagedQuantity(0)
                            .missingQuantity(0)
                            .build());
                }
            }
        }

        // 2. Xử lý Đồ vải & Tài sản Mất hoặc Hỏng nặng
        if (request.getDamagedOrLostAssets() != null) {
            for (SubmitRoomInspectionRequest.AssetIncidentReportDto asset : request.getDamagedOrLostAssets()) {
                int qty = asset.getQuantity() != null && asset.getQuantity() > 0 ? asset.getQuantity() : 1;
                BigDecimal price = asset.getCompensationPrice() != null ? asset.getCompensationPrice() : BigDecimal.ZERO;
                BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

                RoomConsumptionItemType type = asset.getIncidentType() != null 
                        ? asset.getIncidentType() 
                        : RoomConsumptionItemType.ASSET_DAMAGED;

                RoomConsumptionRecord record = RoomConsumptionRecord.builder()
                        .housekeepingTask(task)
                        .booking(task.getBooking())
                        .villa(task.getVilla())
                        .room(task.getRoom())
                        .itemType(type)
                        .itemName(asset.getItemName() != null ? asset.getItemName() : "Tài sản phòng")
                        .quantity(qty)
                        .unitPrice(price)
                        .totalPrice(total)
                        .evidencePhotoUrl(asset.getEvidencePhotoUrl())
                        .note(asset.getNote())
                        .status(RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL)
                        .recordedBy(staffEmail)
                        .build();

                createdRecords.add(consumptionRecordRepository.save(record));

                if (asset.getInventoryItemId() != null) {
                    refillItems.add(RefillItemCheckRequest.builder()
                            .itemId(asset.getInventoryItemId())
                            .actualQuantity(0)
                            .consumedQuantity(0)
                            .damagedQuantity(type == RoomConsumptionItemType.ASSET_DAMAGED ? qty : 0)
                            .missingQuantity(type == RoomConsumptionItemType.ASSET_LOST ? qty : 0)
                            .build());
                }
            }
        }

        // 3. Tự động sinh phiếu Refill kho nếu có vật tư thiếu
        if (!refillItems.isEmpty() && task.getVilla() != null) {
            try {
                CreateRefillTaskRequest refillReq = CreateRefillTaskRequest.builder()
                        .villaId(task.getVilla().getId())
                        .housekeepingTaskId(task.getId())
                        .note("Yêu cầu bù đồ tự động từ kết quả kiểm kê dọn phòng: " + (task.getRoom() != null ? task.getRoom().getRoomNumber() : task.getVilla().getVillaNumber()))
                        .items(refillItems)
                        .build();
                adminRefillService.createRefillTask(refillReq, staffEmail);
                log.info("Successfully created automatic RefillTask for villa {}", task.getVilla().getId());
            } catch (Exception ex) {
                log.warn("Could not create automatic refill task: {}", ex.getMessage());
            }
        }

        return createdRecords.stream()
                .map(RoomConsumptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomConsumptionResponse> getConsumptionsByTask(Long taskId) {
        return consumptionRecordRepository.findByHousekeepingTaskId(taskId).stream()
                .map(RoomConsumptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomConsumptionResponse> getPendingConsumptionsByBooking(Long bookingId) {
        return consumptionRecordRepository.findByBookingIdAndStatus(bookingId, RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL).stream()
                .map(RoomConsumptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomConsumptionResponse approveConsumption(Long consumptionId, String receptionistEmail) {
        RoomConsumptionRecord record = consumptionRecordRepository.findById(consumptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi tiêu thụ ID: " + consumptionId));

        if (record.getStatus() == RoomConsumptionStatus.APPROVED_CHARGED) {
            return RoomConsumptionResponse.fromEntity(record);
        }

        record.setStatus(RoomConsumptionStatus.APPROVED_CHARGED);
        record.setApprovedBy(receptionistEmail);

        // Cộng vào số dư Folio của Booking nếu có
        if (record.getBooking() != null) {
            Booking booking = record.getBooking();
            BigDecimal currentFolio = booking.getFolioBalance() != null ? booking.getFolioBalance() : BigDecimal.ZERO;
            booking.setFolioBalance(currentFolio.add(record.getTotalPrice()));
            bookingRepository.save(booking);
            log.info("Updated booking {} folioBalance to {}", booking.getBookingCode(), booking.getFolioBalance());
        }

        RoomConsumptionRecord saved = consumptionRecordRepository.save(record);
        return RoomConsumptionResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RoomConsumptionResponse waiveConsumption(Long consumptionId, String reason, String receptionistEmail) {
        RoomConsumptionRecord record = consumptionRecordRepository.findById(consumptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi tiêu thụ ID: " + consumptionId));

        record.setStatus(RoomConsumptionStatus.WAIVED);
        record.setApprovedBy(receptionistEmail);
        String existingNote = record.getNote() != null ? record.getNote() + " | " : "";
        record.setNote(existingNote + "Lễ tân miễn giảm: " + (reason != null ? reason : "Không tính phí"));

        RoomConsumptionRecord saved = consumptionRecordRepository.save(record);
        return RoomConsumptionResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public LostAndFoundResponse createLostAndFound(LostAndFoundRequest request, String finderEmail) {
        Villa villa = villaRepository.findById(request.getVillaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Villa ID: " + request.getVillaId()));
        Room room = request.getRoomId() != null ? roomRepository.findById(request.getRoomId()).orElse(null) : null;
        Booking booking = request.getBookingId() != null ? bookingRepository.findById(request.getBookingId()).orElse(null) : null;

        String code = "LF-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        LostAndFoundItem item = LostAndFoundItem.builder()
                .itemCode(code)
                .villa(villa)
                .room(room)
                .booking(booking)
                .itemName(request.getItemName())
                .category(request.getCategory() != null ? request.getCategory() : "OTHER")
                .foundLocation(request.getFoundLocation())
                .photoUrl(request.getPhotoUrl())
                .finderName(finderEmail != null ? finderEmail : "Nhân viên buồng phòng")
                .guestName(request.getGuestName())
                .guestPhone(request.getGuestPhone())
                .status(LostAndFoundStatus.STORED)
                .note(request.getNote())
                .build();

        return LostAndFoundResponse.fromEntity(lostAndFoundItemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LostAndFoundResponse> getLostAndFoundList(LostAndFoundStatus status, Long villaId) {
        List<LostAndFoundItem> list;
        if (villaId != null) {
            list = lostAndFoundItemRepository.findByVillaId(villaId);
        } else if (status != null) {
            list = lostAndFoundItemRepository.findByStatus(status);
        } else {
            list = lostAndFoundItemRepository.findAll();
        }
        return list.stream().map(LostAndFoundResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LostAndFoundResponse updateLostAndFoundStatus(Long id, LostAndFoundStatus status, String note) {
        LostAndFoundItem item = lostAndFoundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đồ thất lạc ID: " + id));

        item.setStatus(status);
        if (status == LostAndFoundStatus.RETURNED) {
            item.setReturnedAt(LocalDateTime.now());
        }
        if (note != null && !note.isBlank()) {
            item.setNote((item.getNote() != null ? item.getNote() + " | " : "") + note);
        }
        return LostAndFoundResponse.fromEntity(lostAndFoundItemRepository.save(item));
    }

    @Override
    @Transactional
    public MaintenanceTicketResponse createMaintenanceTicket(MaintenanceTicketRequest request, String reporterEmail) {
        Villa villa = villaRepository.findById(request.getVillaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Villa ID: " + request.getVillaId()));
        Room room = request.getRoomId() != null ? roomRepository.findById(request.getRoomId()).orElse(null) : null;

        String code = "MT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        MaintenanceTicket ticket = MaintenanceTicket.builder()
                .ticketCode(code)
                .villa(villa)
                .room(room)
                .category(request.getCategory())
                .priority(request.getPriority() != null ? request.getPriority() : MaintenancePriority.MEDIUM)
                .description(request.getDescription())
                .photoUrl(request.getPhotoUrl())
                .status(MaintenanceStatus.REPORTED)
                .reportedBy(reporterEmail != null ? reporterEmail : "Nhân viên buồng phòng")
                .build();

        return MaintenanceTicketResponse.fromEntity(maintenanceTicketRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceTicketResponse> getMaintenanceTickets(MaintenanceStatus status, Long villaId) {
        List<MaintenanceTicket> list;
        if (villaId != null) {
            list = maintenanceTicketRepository.findByVillaId(villaId);
        } else if (status != null) {
            list = maintenanceTicketRepository.findByStatus(status);
        } else {
            list = maintenanceTicketRepository.findAll();
        }
        return list.stream().map(MaintenanceTicketResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaintenanceTicketResponse updateMaintenanceTicketStatus(Long id, MaintenanceStatus status, String technicianNote, String techName) {
        MaintenanceTicket ticket = maintenanceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket bảo trì ID: " + id));

        ticket.setStatus(status);
        if (status == MaintenanceStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        if (technicianNote != null && !technicianNote.isBlank()) {
            ticket.setTechnicianNote(technicianNote);
        }
        if (techName != null && !techName.isBlank()) {
            ticket.setTechnicianName(techName);
        }
        return MaintenanceTicketResponse.fromEntity(maintenanceTicketRepository.save(ticket));
    }
}
