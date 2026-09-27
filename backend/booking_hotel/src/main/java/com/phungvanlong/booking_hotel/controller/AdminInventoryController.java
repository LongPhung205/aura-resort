package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.InventoryItemRequest;
import com.phungvanlong.booking_hotel.dto.request.InventoryTransactionRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaAssetRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.InventoryItemResponse;
import com.phungvanlong.booking_hotel.dto.response.InventorySummaryResponse;
import com.phungvanlong.booking_hotel.dto.response.InventoryTransactionResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaAssetResponse;
import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.VillaAssetStatus;
import com.phungvanlong.booking_hotel.service.AdminInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final AdminInventoryService inventoryService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<InventorySummaryResponse>> getSummary() {
        InventorySummaryResponse summary = inventoryService.getSummary();
        return ResponseEntity.ok(ApiResponse.success(summary, "Lấy số liệu tổng quan kho vật tư thành công"));
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getItems(
            @RequestParam(required = false) InventoryCategory category,
            @RequestParam(required = false) String keyword) {
        List<InventoryItemResponse> items = inventoryService.getItems(category, keyword);
        return ResponseEntity.ok(ApiResponse.success(items, "Lấy danh sách vật tư thành công"));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> getItemById(@PathVariable Long id) {
        InventoryItemResponse item = inventoryService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success(item, "Lấy chi tiết vật tư thành công"));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> createItem(
            @Valid @RequestBody InventoryItemRequest request) {
        InventoryItemResponse created = inventoryService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Thêm mới vật tư vào kho thành công"));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryItemRequest request) {
        InventoryItemResponse updated = inventoryService.updateItem(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật vật tư thành công"));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa vật tư khỏi hệ thống thành công"));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactions(
            @RequestParam(required = false) Long itemId) {
        List<InventoryTransactionResponse> list = inventoryService.getTransactions(itemId);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy nhật ký giao dịch kho thành công"));
    }

    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> recordTransaction(
            @Valid @RequestBody InventoryTransactionRequest request,
            Authentication authentication) {
        String performerEmail = authentication != null ? authentication.getName() : null;
        InventoryTransactionResponse response = inventoryService.recordTransaction(request, performerEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Ghi nhận giao dịch kho thành công"));
    }

    @GetMapping("/villa-assets")
    public ResponseEntity<ApiResponse<List<VillaAssetResponse>>> getVillaAssets(
            @RequestParam(required = false) Long villaId,
            @RequestParam(required = false) VillaAssetStatus status) {
        List<VillaAssetResponse> assets = inventoryService.getVillaAssets(villaId, status);
        return ResponseEntity.ok(ApiResponse.success(assets, "Lấy danh mục tài sản villa thành công"));
    }

    @PostMapping("/villa-assets")
    public ResponseEntity<ApiResponse<VillaAssetResponse>> createVillaAsset(
            @Valid @RequestBody VillaAssetRequest request) {
        VillaAssetResponse response = inventoryService.createVillaAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Thêm tài sản villa thành công"));
    }

    @PutMapping("/villa-assets/{id}")
    public ResponseEntity<ApiResponse<VillaAssetResponse>> updateVillaAsset(
            @PathVariable Long id,
            @Valid @RequestBody VillaAssetRequest request) {
        VillaAssetResponse response = inventoryService.updateVillaAsset(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật tài sản villa thành công"));
    }

    @DeleteMapping("/villa-assets/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVillaAsset(@PathVariable Long id) {
        inventoryService.deleteVillaAsset(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài sản villa thành công"));
    }
}
