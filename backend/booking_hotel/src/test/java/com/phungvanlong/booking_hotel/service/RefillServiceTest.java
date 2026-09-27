package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.CreateRefillTaskRequest;
import com.phungvanlong.booking_hotel.dto.request.InventoryItemRequest;
import com.phungvanlong.booking_hotel.dto.request.RefillItemCheckRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaSupplyStandardRequest;
import com.phungvanlong.booking_hotel.dto.response.InventoryItemResponse;
import com.phungvanlong.booking_hotel.dto.response.RefillTaskResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaInventoryResponse;
import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.RefillTaskStatus;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaType;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.repository.VillaTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RefillServiceTest {

    @Autowired
    private AdminRefillService refillService;

    @Autowired
    private AdminInventoryService inventoryService;

    @Autowired
    private VillaRepository villaRepository;

    @Autowired
    private VillaTypeRepository villaTypeRepository;

    private Villa testVilla;
    private InventoryItemResponse waterItem;
    private InventoryItemResponse towelItem;

    @BeforeEach
    void setUp() {
        VillaType vt = villaTypeRepository.save(VillaType.builder()
                .name("Villa Test Type")
                .basePrice(new BigDecimal("5000000"))
                .adults(4)
                .children(2)
                .capacity(6)
                .build());

        testVilla = villaRepository.save(Villa.builder()
                .villaNumber("Villa-RF-01")
                .villaType(vt)
                .floor(2)
                .build());

        waterItem = inventoryService.createItem(InventoryItemRequest.builder()
                .code("TEST-RF-WATER")
                .name("Nước khoáng núi lửa 500ml")
                .category(InventoryCategory.MINIBAR)
                .unit("Chai")
                .inStock(100)
                .minThreshold(20)
                .unitPrice(new BigDecimal("15000"))
                .build());

        towelItem = inventoryService.createItem(InventoryItemRequest.builder()
                .code("TEST-RF-TOWEL")
                .name("Khăn tắm dệt logo Aura")
                .category(InventoryCategory.LINEN)
                .unit("Chiếc")
                .inStock(50)
                .minThreshold(10)
                .unitPrice(new BigDecimal("120000"))
                .build());

        // Thiết lập định mức: Nước = 6, Khăn = 4
        refillService.saveStandard(VillaSupplyStandardRequest.builder()
                .villaId(testVilla.getId())
                .itemId(waterItem.getId())
                .standardQuantity(6)
                .note("Tiêu chuẩn minibar phòng ngủ chính")
                .build());

        refillService.saveStandard(VillaSupplyStandardRequest.builder()
                .villaId(testVilla.getId())
                .itemId(towelItem.getId())
                .standardQuantity(4)
                .note("Khăn tắm thay mới sau mỗi lượt khách")
                .build());
    }

    @Test
    @DisplayName("Kiểm kê sau checkout: Hệ thống tự động tính Refill = max(0, Standard - Actual)")
    void testCreateRefillTaskCalculatesRefillQuantityCorrectly() {
        // Thực tế còn lại: Nước = 2 (thiếu 4), Khăn = 3 (thiếu 1)
        CreateRefillTaskRequest req = CreateRefillTaskRequest.builder()
                .villaId(testVilla.getId())
                .assignedStaff("Housekeeper Nguyễn Văn A")
                .note("Khách đoàn VIP checkout lúc 12:00")
                .items(List.of(
                        RefillItemCheckRequest.builder()
                                .itemId(waterItem.getId())
                                .actualQuantity(2)
                                .consumedQuantity(4)
                                .build(),
                        RefillItemCheckRequest.builder()
                                .itemId(towelItem.getId())
                                .actualQuantity(3)
                                .consumedQuantity(1)
                                .build()
                ))
                .build();

        RefillTaskResponse task = refillService.createRefillTask(req, "supervisor@auraholdings.vn");

        assertNotNull(task.getId());
        assertEquals(RefillTaskStatus.PENDING, task.getStatus());
        assertEquals(2, task.getItems().size());

        // Kiểm tra vật tư Nước
        var waterTaskItem = task.getItems().stream()
                .filter(i -> i.getItemId().equals(waterItem.getId()))
                .findFirst().orElseThrow();
        assertEquals(6, waterTaskItem.getStandardQuantity());
        assertEquals(2, waterTaskItem.getActualQuantity());
        assertEquals(4, waterTaskItem.getRefillQuantity()); // 6 - 2 = 4

        // Kiểm tra vật tư Khăn
        var towelTaskItem = task.getItems().stream()
                .filter(i -> i.getItemId().equals(towelItem.getId()))
                .findFirst().orElseThrow();
        assertEquals(4, towelTaskItem.getStandardQuantity());
        assertEquals(3, towelTaskItem.getActualQuantity());
        assertEquals(1, towelTaskItem.getRefillQuantity()); // 4 - 3 = 1

        assertEquals(5, task.getTotalRefillUnits()); // 4 + 1 = 5
    }

    @Test
    @DisplayName("Xuất kho hoàn tất: Trừ kho trung tâm, cập nhật kho Villa về đủ định mức chuẩn")
    void testFulfillRefillTaskDeductsCentralWarehouseAndUpdatesVilla() {
        CreateRefillTaskRequest req = CreateRefillTaskRequest.builder()
                .villaId(testVilla.getId())
                .items(List.of(
                        RefillItemCheckRequest.builder()
                                .itemId(waterItem.getId())
                                .actualQuantity(2) // Cần refill 4
                                .build(),
                        RefillItemCheckRequest.builder()
                                .itemId(towelItem.getId())
                                .actualQuantity(3) // Cần refill 1
                                .build()
                ))
                .build();

        RefillTaskResponse createdTask = refillService.createRefillTask(req, "supervisor@auraholdings.vn");

        // Thực hiện xuất kho & hoàn tất bổ sung
        RefillTaskResponse fulfilled = refillService.fulfillRefillTask(createdTask.getId(), "warehouse@auraholdings.vn");
        assertEquals(RefillTaskStatus.COMPLETED, fulfilled.getStatus());
        assertNotNull(fulfilled.getCompletedAt());

        // Kiểm tra kho trung tâm đã bị trừ
        InventoryItemResponse updatedWater = inventoryService.getItemById(waterItem.getId());
        assertEquals(96, updatedWater.getInStock()); // 100 - 4 = 96

        InventoryItemResponse updatedTowel = inventoryService.getItemById(towelItem.getId());
        assertEquals(49, updatedTowel.getInStock()); // 50 - 1 = 49

        // Kiểm tra kho Villa đã được hồi phục về đủ định mức chuẩn (Nước: 6, Khăn: 4)
        List<VillaInventoryResponse> villaStock = refillService.getVillaInventory(testVilla.getId());
        var waterStock = villaStock.stream().filter(s -> s.getItemId().equals(waterItem.getId())).findFirst().orElseThrow();
        assertEquals(6, waterStock.getCurrentQuantity());
        assertEquals(0, waterStock.getDeficitQuantity());
        assertEquals(100, waterStock.getFillPercentage());

        var towelStock = villaStock.stream().filter(s -> s.getItemId().equals(towelItem.getId())).findFirst().orElseThrow();
        assertEquals(4, towelStock.getCurrentQuantity());
        assertEquals(0, towelStock.getDeficitQuantity());
    }

    @Test
    @DisplayName("Xuất kho thất bại nếu kho trung tâm không đủ hàng")
    void testFulfillRefillTaskFailsWhenWarehouseStockInsufficient() {
        // Tạo vật tư tồn kho = 1
        InventoryItemResponse rareItem = inventoryService.createItem(InventoryItemRequest.builder()
                .code("TEST-RF-RARE")
                .name("Bộ trà thượng hạng Tây Bắc")
                .category(InventoryCategory.MINIBAR)
                .inStock(1) // Chỉ có 1 hộp trong kho
                .build());

        refillService.saveStandard(VillaSupplyStandardRequest.builder()
                .villaId(testVilla.getId())
                .itemId(rareItem.getId())
                .standardQuantity(5) // Định mức là 5
                .build());

        CreateRefillTaskRequest req = CreateRefillTaskRequest.builder()
                .villaId(testVilla.getId())
                .items(List.of(
                        RefillItemCheckRequest.builder()
                                .itemId(rareItem.getId())
                                .actualQuantity(0) // Cần refill 5, nhưng kho chỉ có 1
                                .build()
                ))
                .build();

        RefillTaskResponse task = refillService.createRefillTask(req, "supervisor@auraholdings.vn");

        assertThrows(BusinessException.class, () -> refillService.fulfillRefillTask(task.getId(), "warehouse@auraholdings.vn"));
    }
}
