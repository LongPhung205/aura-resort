package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.InventoryItemRequest;
import com.phungvanlong.booking_hotel.dto.request.InventoryTransactionRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaAssetRequest;
import com.phungvanlong.booking_hotel.dto.response.InventoryItemResponse;
import com.phungvanlong.booking_hotel.dto.response.InventorySummaryResponse;
import com.phungvanlong.booking_hotel.dto.response.InventoryTransactionResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaAssetResponse;
import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.InventoryTransactionType;
import com.phungvanlong.booking_hotel.entity.VillaAssetStatus;
import com.phungvanlong.booking_hotel.exception.BusinessException;
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
class InventoryServiceTest {

    @Autowired
    private AdminInventoryService inventoryService;

    @Test
    @DisplayName("Tạo mới vật tư và tra cứu tồn kho")
    void testCreateAndGetInventoryItem() {
        InventoryItemRequest req = InventoryItemRequest.builder()
                .code("TEST-TOOTH-99")
                .name("Bàn chải tre hữu cơ Test")
                .category(InventoryCategory.AMENITY)
                .unit("Bộ")
                .inStock(50)
                .minThreshold(20)
                .unitPrice(new BigDecimal("15000"))
                .supplier("GreenEco")
                .location("Kho A1")
                .build();

        InventoryItemResponse created = inventoryService.createItem(req);
        assertNotNull(created.getId());
        assertEquals("TEST-TOOTH-99", created.getCode());
        assertEquals(50, created.getInStock());
        assertFalse(created.isLowStock());

        List<InventoryItemResponse> list = inventoryService.getItems(InventoryCategory.AMENITY, "tre");
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(i -> i.getCode().equals("TEST-TOOTH-99")));
    }

    @Test
    @DisplayName("Nhập kho làm tăng số lượng tồn kho")
    void testRecordImportTransaction() {
        InventoryItemRequest itemReq = InventoryItemRequest.builder()
                .code("TEST-LIN-01")
                .name("Khăn lau tay VIP")
                .category(InventoryCategory.LINEN)
                .unit("Chiếc")
                .inStock(10)
                .minThreshold(5)
                .unitPrice(new BigDecimal("25000"))
                .build();
        InventoryItemResponse item = inventoryService.createItem(itemReq);

        InventoryTransactionRequest txReq = InventoryTransactionRequest.builder()
                .itemId(item.getId())
                .type(InventoryTransactionType.IMPORT)
                .quantity(30)
                .unitPrice(new BigDecimal("25000"))
                .reason("Nhập thêm hàng dự phòng")
                .performer("Thủ kho A")
                .build();

        InventoryTransactionResponse tx = inventoryService.recordTransaction(txReq, "admin@auraholdings.vn");
        assertNotNull(tx.getId());
        assertEquals(30, tx.getQuantity());

        InventoryItemResponse updated = inventoryService.getItemById(item.getId());
        assertEquals(40, updated.getInStock()); // 10 + 30
    }

    @Test
    @DisplayName("Xuất kho làm giảm số lượng tồn kho")
    void testRecordExportTransaction() {
        InventoryItemRequest itemReq = InventoryItemRequest.builder()
                .code("TEST-MNB-01")
                .name("Bia Heineken lon")
                .category(InventoryCategory.MINIBAR)
                .unit("Lon")
                .inStock(25)
                .minThreshold(5)
                .unitPrice(new BigDecimal("35000"))
                .build();
        InventoryItemResponse item = inventoryService.createItem(itemReq);

        InventoryTransactionRequest txReq = InventoryTransactionRequest.builder()
                .itemId(item.getId())
                .type(InventoryTransactionType.EXPORT)
                .quantity(10)
                .reason("Bổ sung tủ minibar Villa #101")
                .performer("Butler B")
                .build();

        inventoryService.recordTransaction(txReq, "admin@auraholdings.vn");

        InventoryItemResponse updated = inventoryService.getItemById(item.getId());
        assertEquals(15, updated.getInStock()); // 25 - 10
    }

    @Test
    @DisplayName("Xuất kho vượt quá số lượng tồn bị từ chối bằng BusinessException")
    void testRecordExportInsufficientStock() {
        InventoryItemRequest itemReq = InventoryItemRequest.builder()
                .code("TEST-OZONE-01")
                .name("Bình xịt khử trùng")
                .category(InventoryCategory.CLEANING)
                .unit("Chai")
                .inStock(5)
                .minThreshold(2)
                .unitPrice(new BigDecimal("50000"))
                .build();
        InventoryItemResponse item = inventoryService.createItem(itemReq);

        InventoryTransactionRequest txReq = InventoryTransactionRequest.builder()
                .itemId(item.getId())
                .type(InventoryTransactionType.EXPORT)
                .quantity(10) // Lớn hơn 5
                .reason("Cấp phát quá mức")
                .build();

        assertThrows(BusinessException.class, () -> inventoryService.recordTransaction(txReq, "admin@auraholdings.vn"));
    }

    @Test
    @DisplayName("Quản lý tài sản trang thiết bị Villa")
    void testVillaAssetCrud() {
        VillaAssetRequest req = VillaAssetRequest.builder()
                .villaNumber("Villa #101")
                .assetName("Máy lọc không khí Dyson Pure Cool")
                .serialNumber("SN-DYSON-9921")
                .category("Điện tử")
                .status(VillaAssetStatus.GOOD)
                .note("Đặt tại phòng khách")
                .build();

        VillaAssetResponse created = inventoryService.createVillaAsset(req);
        assertNotNull(created.getId());
        assertEquals("Máy lọc không khí Dyson Pure Cool", created.getAssetName());
        assertEquals(VillaAssetStatus.GOOD, created.getStatus());

        // Update status to MAINTENANCE
        req.setStatus(VillaAssetStatus.MAINTENANCE);
        VillaAssetResponse updated = inventoryService.updateVillaAsset(created.getId(), req);
        assertEquals(VillaAssetStatus.MAINTENANCE, updated.getStatus());

        // Delete asset
        inventoryService.deleteVillaAsset(created.getId());
        List<VillaAssetResponse> list = inventoryService.getVillaAssets(null, null);
        assertFalse(list.stream().anyMatch(a -> a.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Tổng hợp KPI Kho vật tư")
    void testInventorySummary() {
        InventorySummaryResponse summary = inventoryService.getSummary();
        assertNotNull(summary);
        assertTrue(summary.getTotalItems() >= 0);
        assertNotNull(summary.getTotalInventoryValue());
    }
}
