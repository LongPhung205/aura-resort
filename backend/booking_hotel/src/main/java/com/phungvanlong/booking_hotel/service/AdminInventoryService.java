package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.InventoryItemRequest;
import com.phungvanlong.booking_hotel.dto.request.InventoryTransactionRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaAssetRequest;
import com.phungvanlong.booking_hotel.dto.response.InventoryItemResponse;
import com.phungvanlong.booking_hotel.dto.response.InventorySummaryResponse;
import com.phungvanlong.booking_hotel.dto.response.InventoryTransactionResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaAssetResponse;
import com.phungvanlong.booking_hotel.entity.InventoryCategory;
import com.phungvanlong.booking_hotel.entity.VillaAssetStatus;

import java.util.List;

public interface AdminInventoryService {

    InventorySummaryResponse getSummary();

    List<InventoryItemResponse> getItems(InventoryCategory category, String keyword);

    InventoryItemResponse getItemById(Long id);

    InventoryItemResponse createItem(InventoryItemRequest request);

    InventoryItemResponse updateItem(Long id, InventoryItemRequest request);

    void deleteItem(Long id);

    List<InventoryTransactionResponse> getTransactions(Long itemId);

    InventoryTransactionResponse recordTransaction(InventoryTransactionRequest request, String performerEmail);

    List<VillaAssetResponse> getVillaAssets(Long villaId, VillaAssetStatus status);

    VillaAssetResponse createVillaAsset(VillaAssetRequest request);

    VillaAssetResponse updateVillaAsset(Long id, VillaAssetRequest request);

    void deleteVillaAsset(Long id);
}
