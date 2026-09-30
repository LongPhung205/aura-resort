package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.InventoryItemRequest;
import com.phungvanlong.booking_hotel.dto.request.InventoryTransactionRequest;
import com.phungvanlong.booking_hotel.dto.request.VillaAssetRequest;
import com.phungvanlong.booking_hotel.dto.response.InventoryItemResponse;
import com.phungvanlong.booking_hotel.dto.response.InventorySummaryResponse;
import com.phungvanlong.booking_hotel.dto.response.InventoryTransactionResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaAssetResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.InventoryItemRepository;
import com.phungvanlong.booking_hotel.repository.InventoryTransactionRepository;
import com.phungvanlong.booking_hotel.repository.VillaAssetRepository;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.service.AdminInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInventoryServiceImpl implements AdminInventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final VillaAssetRepository villaAssetRepository;
    private final VillaRepository villaRepository;

    @Override
    @Transactional(readOnly = true)
    public InventorySummaryResponse getSummary() {
        List<InventoryItem> allItems = inventoryItemRepository.findAll();
        long totalItems = allItems.size();
        long lowStockCount = allItems.stream()
                .filter(i -> i.getInStock() != null && i.getMinThreshold() != null && i.getInStock() <= i.getMinThreshold())
                .count();

        BigDecimal totalVal = allItems.stream()
                .map(i -> {
                    BigDecimal price = i.getUnitPrice() != null ? i.getUnitPrice() : BigDecimal.ZERO;
                    int stock = i.getInStock() != null ? i.getInStock() : 0;
                    return price.multiply(BigDecimal.valueOf(stock));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalTx = inventoryTransactionRepository.count();
        long totalAssets = villaAssetRepository.count();

        return InventorySummaryResponse.builder()
                .totalItems(totalItems)
                .lowStockCount(lowStockCount)
                .totalInventoryValue(totalVal)
                .totalTransactions(totalTx)
                .totalVillaAssets(totalAssets)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getItems(InventoryCategory category, String keyword) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        List<InventoryItem> items = inventoryItemRepository.searchItems(category, cleanKeyword);
        return items.stream()
                .map(InventoryItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponse getItemById(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư có ID: " + id));
        return InventoryItemResponse.fromEntity(item);
    }

    @Override
    @Transactional
    public InventoryItemResponse createItem(InventoryItemRequest request) {
        String code = request.getCode();
        if (code == null || code.trim().isEmpty()) {
            code = generateItemCode(request.getCategory());
        } else {
            code = code.trim().toUpperCase();
            if (inventoryItemRepository.existsByCode(code)) {
                throw new BusinessException("Mã vật tư '" + code + "' đã tồn tại trong kho!");
            }
        }

        InventoryItem item = InventoryItem.builder()
                .code(code)
                .name(request.getName().trim())
                .category(request.getCategory())
                .unit(request.getUnit() != null ? request.getUnit().trim() : "Chiếc")
                .inStock(request.getInStock() != null ? request.getInStock() : 0)
                .minThreshold(request.getMinThreshold() != null ? request.getMinThreshold() : 10)
                .unitPrice(request.getUnitPrice() != null ? request.getUnitPrice() : BigDecimal.ZERO)
                .supplier(request.getSupplier())
                .location(request.getLocation() != null ? request.getLocation().trim() : "Kho Tổng A1")
                .description(request.getDescription())
                .build();

        InventoryItem saved = inventoryItemRepository.save(item);
        log.info("Khởi tạo vật tư mới: {} - {}", saved.getCode(), saved.getName());
        return InventoryItemResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public InventoryItemResponse updateItem(Long id, InventoryItemRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư có ID: " + id));

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            String newCode = request.getCode().trim().toUpperCase();
            if (!newCode.equals(item.getCode()) && inventoryItemRepository.existsByCode(newCode)) {
                throw new BusinessException("Mã vật tư '" + newCode + "' đã tồn tại!");
            }
            item.setCode(newCode);
        }

        item.setName(request.getName().trim());
        if (request.getCategory() != null) {
            item.setCategory(request.getCategory());
        }
        if (request.getUnit() != null) {
            item.setUnit(request.getUnit().trim());
        }
        // Số lượng tồn kho không được sửa trực tiếp khi cập nhật thông tin vật tư.
        // Tồn kho chỉ được tăng/giảm thông qua phiếu Nhập / Xuất kho (recordTransaction).
        if (request.getMinThreshold() != null) {
            item.setMinThreshold(request.getMinThreshold());
        }
        if (request.getUnitPrice() != null) {
            item.setUnitPrice(request.getUnitPrice());
        }
        if (request.getSupplier() != null) {
            item.setSupplier(request.getSupplier().trim());
        }
        if (request.getLocation() != null) {
            item.setLocation(request.getLocation().trim());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }

        InventoryItem updated = inventoryItemRepository.save(item);
        return InventoryItemResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư có ID: " + id));
        inventoryItemRepository.delete(item);
        log.info("Đã xóa vật tư: {}", item.getCode());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactions(Long itemId) {
        List<InventoryTransaction> list = (itemId != null)
                ? inventoryTransactionRepository.findByItemIdOrderByCreatedAtDesc(itemId)
                : inventoryTransactionRepository.findTop50ByOrderByCreatedAtDesc();
        return list.stream()
                .map(InventoryTransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryTransactionResponse recordTransaction(InventoryTransactionRequest request, String performerEmail) {
        InventoryItem item = inventoryItemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư có ID: " + request.getItemId()));

        int qty = request.getQuantity();
        BigDecimal price = (request.getUnitPrice() != null && request.getUnitPrice().compareTo(BigDecimal.ZERO) > 0)
                ? request.getUnitPrice()
                : item.getUnitPrice();
        if (price == null) {
            price = BigDecimal.ZERO;
        }
        BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

        // Cập nhật tồn kho theo loại nghiệp vụ
        int currentStock = item.getInStock() != null ? item.getInStock() : 0;
        switch (request.getType()) {
            case IMPORT -> {
                item.setInStock(currentStock + qty);
                if (request.getUnitPrice() != null && request.getUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
                    item.setUnitPrice(request.getUnitPrice());
                }
            }
            case EXPORT -> {
                if (currentStock < qty) {
                    throw new BusinessException("Số lượng tồn kho không đủ để xuất! Hiện có: " + currentStock + ", yêu cầu: " + qty);
                }
                item.setInStock(currentStock - qty);
            }
            case ADJUSTMENT -> {
                item.setInStock(qty);
            }
        }
        inventoryItemRepository.save(item);

        Villa destVilla = null;
        if (request.getDestinationVillaId() != null) {
            destVilla = villaRepository.findById(request.getDestinationVillaId()).orElse(null);
        }

        String performer = (request.getPerformer() != null && !request.getPerformer().trim().isEmpty())
                ? request.getPerformer().trim()
                : (performerEmail != null ? performerEmail : "Ban Quản Lý Kho");

        InventoryTransaction tx = InventoryTransaction.builder()
                .item(item)
                .type(request.getType())
                .quantity(qty)
                .unitPrice(price)
                .totalAmount(total)
                .reason(request.getReason())
                .performer(performer)
                .destinationVilla(destVilla)
                .build();

        InventoryTransaction savedTx = inventoryTransactionRepository.save(tx);
        log.info("Ghi nhận giao dịch kho: {} x {} ({})", savedTx.getType(), qty, item.getCode());
        return InventoryTransactionResponse.fromEntity(savedTx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VillaAssetResponse> getVillaAssets(Long villaId, VillaAssetStatus status) {
        List<VillaAsset> assets;
        if (villaId != null) {
            assets = villaAssetRepository.findByVillaIdOrderByCreatedAtDesc(villaId);
        } else if (status != null) {
            assets = villaAssetRepository.findByStatus(status);
        } else {
            assets = villaAssetRepository.findAllByOrderByCreatedAtDesc();
        }
        return assets.stream()
                .map(VillaAssetResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VillaAssetResponse createVillaAsset(VillaAssetRequest request) {
        Villa villa = null;
        String vNumber = request.getVillaNumber();
        if (request.getVillaId() != null) {
            villa = villaRepository.findById(request.getVillaId()).orElse(null);
            if (villa != null) {
                vNumber = villa.getVillaNumber();
                if (villa.getZone() != null) {
                    vNumber += " (" + villa.getZone().getName() + ")";
                }
            }
        }

        VillaAsset asset = VillaAsset.builder()
                .villa(villa)
                .villaNumber(vNumber)
                .assetName(request.getAssetName().trim())
                .serialNumber(request.getSerialNumber() != null ? request.getSerialNumber().trim() : null)
                .category(request.getCategory() != null ? request.getCategory().trim() : "Trang thiết bị")
                .status(request.getStatus() != null ? request.getStatus() : VillaAssetStatus.GOOD)
                .installDate(request.getInstallDate())
                .warrantyExpiry(request.getWarrantyExpiry())
                .note(request.getNote())
                .build();

        VillaAsset saved = villaAssetRepository.save(asset);
        return VillaAssetResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public VillaAssetResponse updateVillaAsset(Long id, VillaAssetRequest request) {
        VillaAsset asset = villaAssetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài sản có ID: " + id));

        if (request.getVillaId() != null) {
            Villa villa = villaRepository.findById(request.getVillaId()).orElse(null);
            asset.setVilla(villa);
            if (villa != null) {
                String vNumber = villa.getVillaNumber();
                if (villa.getZone() != null) {
                    vNumber += " (" + villa.getZone().getName() + ")";
                }
                asset.setVillaNumber(vNumber);
            }
        } else if (request.getVillaNumber() != null) {
            asset.setVillaNumber(request.getVillaNumber());
        }

        asset.setAssetName(request.getAssetName().trim());
        if (request.getSerialNumber() != null) {
            asset.setSerialNumber(request.getSerialNumber().trim());
        }
        if (request.getCategory() != null) {
            asset.setCategory(request.getCategory().trim());
        }
        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus());
        }
        if (request.getInstallDate() != null) {
            asset.setInstallDate(request.getInstallDate());
        }
        if (request.getWarrantyExpiry() != null) {
            asset.setWarrantyExpiry(request.getWarrantyExpiry());
        }
        if (request.getNote() != null) {
            asset.setNote(request.getNote());
        }

        VillaAsset updated = villaAssetRepository.save(asset);
        return VillaAssetResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteVillaAsset(Long id) {
        VillaAsset asset = villaAssetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài sản có ID: " + id));
        villaAssetRepository.delete(asset);
        log.info("Đã xóa tài sản: {}", asset.getAssetName());
    }

    private String generateItemCode(InventoryCategory category) {
        String prefix = switch (category) {
            case AMENITY -> "AMN";
            case LINEN -> "LIN";
            case MINIBAR -> "MNB";
            case CLEANING -> "CLN";
            case EQUIPMENT -> "EQP";
            case OTHER -> "ITM";
        };
        String code;
        int count = 1;
        do {
            code = String.format("%s-%04d", prefix, System.currentTimeMillis() % 10000 + count);
            count++;
        } while (inventoryItemRepository.existsByCode(code));
        return code;
    }
}
