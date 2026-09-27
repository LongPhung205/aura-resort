package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.CreateRefillTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.RefillItemCheckRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaSupplyStandardRequest;
import com.phungvanlong.booking_hotel.dto.response.RefillTaskResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaInventoryResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaSupplyStandardResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.*;
import com.phungvanlong.booking_hotel.service.AdminRefillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRefillServiceImpl implements AdminRefillService {

    private final VillaSupplyStandardRepository villaSupplyStandardRepository;
    private final VillaInventoryRepository villaInventoryRepository;
    private final RefillTaskRepository refillTaskRepository;
    private final RefillTaskItemRepository refillTaskItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final VillaRepository villaRepository;
    private final HousekeepingTaskRepository housekeepingTaskRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VillaSupplyStandardResponse> getStandardsByVilla(Long villaId) {
        List<VillaSupplyStandard> list = (villaId != null)
                ? villaSupplyStandardRepository.findByVillaIdOrderByItemNameAsc(villaId)
                : villaSupplyStandardRepository.findAll();
        return list.stream()
                .map(VillaSupplyStandardResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VillaSupplyStandardResponse saveStandard(VillaSupplyStandardRequest request) {
        Villa villa = villaRepository.findById(request.getVillaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Villa ID: " + request.getVillaId()));
        InventoryItem item = inventoryItemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư ID: " + request.getItemId()));

        VillaSupplyStandard standard = villaSupplyStandardRepository
                .findByVillaIdAndItemId(request.getVillaId(), request.getItemId())
                .orElse(VillaSupplyStandard.builder()
                        .villa(villa)
                        .item(item)
                        .build());

        standard.setStandardQuantity(request.getStandardQuantity());
        standard.setNote(request.getNote());

        VillaSupplyStandard saved = villaSupplyStandardRepository.save(standard);

        // Đảm bảo có bản ghi VillaInventory tương ứng
        villaInventoryRepository.findByVillaIdAndItemId(villa.getId(), item.getId())
                .orElseGet(() -> villaInventoryRepository.save(VillaInventory.builder()
                        .villa(villa)
                        .item(item)
                        .currentQuantity(request.getStandardQuantity())
                        .lastCheckedAt(LocalDateTime.now())
                        .build()));

        return VillaSupplyStandardResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteStandard(Long id) {
        VillaSupplyStandard standard = villaSupplyStandardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy định mức ID: " + id));
        villaSupplyStandardRepository.delete(standard);
    }

    @Override
    @Transactional
    public void applyDefaultStandardsToVilla(Long villaId) {
        Villa villa = villaRepository.findById(villaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Villa ID: " + villaId));

        List<InventoryItem> items = inventoryItemRepository.findAll();
        for (InventoryItem item : items) {
            if (!villaSupplyStandardRepository.existsByVillaIdAndItemId(villaId, item.getId())) {
                int defaultQty = switch (item.getCategory()) {
                    case AMENITY -> 4;
                    case LINEN -> 4;
                    case MINIBAR -> 2;
                    case CLEANING -> 1;
                    case EQUIPMENT -> 1;
                    case OTHER -> 2;
                };

                villaSupplyStandardRepository.save(VillaSupplyStandard.builder()
                        .villa(villa)
                        .item(item)
                        .standardQuantity(defaultQty)
                        .note("Định mức tự động tiêu chuẩn Resort")
                        .build());

                villaInventoryRepository.findByVillaIdAndItemId(villaId, item.getId())
                        .orElseGet(() -> villaInventoryRepository.save(VillaInventory.builder()
                                .villa(villa)
                                .item(item)
                                .currentQuantity(defaultQty)
                                .lastCheckedAt(LocalDateTime.now())
                                .build()));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VillaInventoryResponse> getVillaInventory(Long villaId) {
        if (villaId == null) {
            return Collections.emptyList();
        }

        List<VillaSupplyStandard> standards = villaSupplyStandardRepository.findByVillaIdOrderByItemNameAsc(villaId);
        Map<Long, Integer> standardMap = standards.stream()
                .collect(Collectors.toMap(s -> s.getItem().getId(), VillaSupplyStandard::getStandardQuantity, (a, b) -> a));

        List<VillaInventory> inventories = villaInventoryRepository.findByVillaIdOrderByItemNameAsc(villaId);
        return inventories.stream()
                .map(inv -> {
                    Integer std = standardMap.get(inv.getItem().getId());
                    return VillaInventoryResponse.fromEntity(inv, std);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RefillTaskResponse createRefillTask(CreateRefillTaskRequest request, String creatorEmail) {
        Villa villa = villaRepository.findById(request.getVillaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Villa ID: " + request.getVillaId()));

        HousekeepingTask hkTask = null;
        if (request.getHousekeepingTaskId() != null) {
            hkTask = housekeepingTaskRepository.findById(request.getHousekeepingTaskId()).orElse(null);
        }

        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String taskCode = String.format("RF-%s-%04d", datePrefix, System.currentTimeMillis() % 10000);

        String creator = (creatorEmail != null && !creatorEmail.isBlank())
                ? creatorEmail
                : "Housekeeping Supervisor";

        RefillTask refillTask = RefillTask.builder()
                .taskCode(taskCode)
                .villa(villa)
                .housekeepingTask(hkTask)
                .status(RefillTaskStatus.PENDING)
                .creator(creator)
                .assignedStaff(request.getAssignedStaff())
                .note(request.getNote())
                .build();

        List<RefillTaskItem> taskItems = new ArrayList<>();
        for (RefillItemCheckRequest itemReq : request.getItems()) {
            InventoryItem item = inventoryItemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư ID: " + itemReq.getItemId()));

            int standardQty = villaSupplyStandardRepository.findByVillaIdAndItemId(villa.getId(), item.getId())
                    .map(VillaSupplyStandard::getStandardQuantity)
                    .orElse(0);

            int actualQty = itemReq.getActualQuantity() != null ? itemReq.getActualQuantity() : 0;
            int refillQty = Math.max(0, standardQty - actualQty);

            int consumed = itemReq.getConsumedQuantity() != null ? itemReq.getConsumedQuantity() : refillQty;
            int damaged = itemReq.getDamagedQuantity() != null ? itemReq.getDamagedQuantity() : 0;
            int missing = itemReq.getMissingQuantity() != null ? itemReq.getMissingQuantity() : 0;

            RefillTaskItem taskItem = RefillTaskItem.builder()
                    .refillTask(refillTask)
                    .item(item)
                    .standardQuantity(standardQty)
                    .actualQuantity(actualQty)
                    .refillQuantity(refillQty)
                    .consumedQuantity(consumed)
                    .damagedQuantity(damaged)
                    .missingQuantity(missing)
                    .isFulfilled(refillQty == 0) // Nếu không cần bù thì coi như đã fulfilled
                    .build();

            taskItems.add(taskItem);

            // Cập nhật số lượng kiểm kê thực tế vào VillaInventory
            VillaInventory vInv = villaInventoryRepository.findByVillaIdAndItemId(villa.getId(), item.getId())
                    .orElse(VillaInventory.builder().villa(villa).item(item).build());
            vInv.setCurrentQuantity(actualQty);
            vInv.setLastCheckedAt(LocalDateTime.now());
            villaInventoryRepository.save(vInv);
        }

        refillTask.setItems(taskItems);
        RefillTask saved = refillTaskRepository.save(refillTask);
        log.info("Khởi tạo nhiệm vụ Refill: {} cho Villa: {}", saved.getTaskCode(), villa.getVillaNumber());
        return RefillTaskResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RefillTaskResponse fulfillRefillTask(Long taskId, String performerEmail) {
        RefillTask task = refillTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ Refill ID: " + taskId));

        if (task.getStatus() == RefillTaskStatus.COMPLETED) {
            throw new BusinessException("Nhiệm vụ Refill " + task.getTaskCode() + " đã được hoàn tất trước đó!");
        }

        // Bước 1: Kiểm tra đủ hàng trong kho trung tâm
        for (RefillTaskItem taskItem : task.getItems()) {
            if (Boolean.FALSE.equals(taskItem.getIsFulfilled()) && taskItem.getRefillQuantity() > 0) {
                InventoryItem item = taskItem.getItem();
                int required = taskItem.getRefillQuantity();
                int inStock = item.getInStock() != null ? item.getInStock() : 0;
                if (inStock < required) {
                    throw new BusinessException(String.format(
                            "Kho trung tâm không đủ số lượng cho '%s' (Mã: %s). Tồn kho: %d, Yêu cầu cấp: %d",
                            item.getName(), item.getCode(), inStock, required));
                }
            }
        }

        String performer = (performerEmail != null && !performerEmail.isBlank())
                ? performerEmail
                : (task.getAssignedStaff() != null ? task.getAssignedStaff() : "Ban Quản Lý Kho");

        // Bước 2: Xuất kho trung tâm, sinh InventoryTransaction, cập nhật kho Villa
        for (RefillTaskItem taskItem : task.getItems()) {
            if (Boolean.FALSE.equals(taskItem.getIsFulfilled()) && taskItem.getRefillQuantity() > 0) {
                InventoryItem item = taskItem.getItem();
                int qty = taskItem.getRefillQuantity();

                // Trừ kho trung tâm
                item.setInStock(item.getInStock() - qty);
                inventoryItemRepository.save(item);

                // Ghi nhận phiếu xuất kho
                BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(qty));

                InventoryTransaction tx = InventoryTransaction.builder()
                        .item(item)
                        .type(InventoryTransactionType.EXPORT)
                        .quantity(qty)
                        .unitPrice(price)
                        .totalAmount(totalAmount)
                        .reason("Cấp phát Refill theo nhiệm vụ " + task.getTaskCode() + " cho " + task.getVilla().getVillaNumber())
                        .performer(performer)
                        .destinationVilla(task.getVilla())
                        .build();
                inventoryTransactionRepository.save(tx);

                // Cập nhật phục hồi kho Villa về định mức chuẩn
                VillaInventory vInv = villaInventoryRepository.findByVillaIdAndItemId(task.getVilla().getId(), item.getId())
                        .orElse(VillaInventory.builder().villa(task.getVilla()).item(item).build());
                int updatedCurrent = (vInv.getCurrentQuantity() != null ? vInv.getCurrentQuantity() : 0) + qty;
                vInv.setCurrentQuantity(updatedCurrent);
                vInv.setLastCheckedAt(LocalDateTime.now());
                villaInventoryRepository.save(vInv);

                taskItem.setIsFulfilled(true);
            }
        }

        task.setStatus(RefillTaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        RefillTask updated = refillTaskRepository.save(task);

        log.info("Hoàn tất xuất kho và bổ sung Refill cho nhiệm vụ: {}", updated.getTaskCode());
        return RefillTaskResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefillTaskResponse> getRefillTasks(RefillTaskStatus status, Long villaId) {
        List<RefillTask> list;
        if (status != null && villaId != null) {
            list = refillTaskRepository.findByVillaIdAndStatusOrderByCreatedAtDesc(villaId, status);
        } else if (status != null) {
            list = refillTaskRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (villaId != null) {
            list = refillTaskRepository.findByVillaIdOrderByCreatedAtDesc(villaId);
        } else {
            list = refillTaskRepository.findAllByOrderByCreatedAtDesc();
        }

        return list.stream()
                .map(RefillTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RefillTaskResponse getRefillTaskById(Long id) {
        RefillTask task = refillTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ Refill ID: " + id));
        return RefillTaskResponse.fromEntity(task);
    }
}
