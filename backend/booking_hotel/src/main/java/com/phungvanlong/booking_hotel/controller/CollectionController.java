package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.CollectionRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.CollectionResponse;
import com.phungvanlong.booking_hotel.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    // Public APIs
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CollectionResponse>>> getActiveCollections() {
        return ResponseEntity.ok(ApiResponse.success(
            collectionService.getActiveCollections(), 
            "Lấy danh sách bộ sưu tập thành công"
        ));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CollectionResponse>> getCollectionBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(
            collectionService.getCollectionBySlug(slug), 
            "Lấy chi tiết bộ sưu tập thành công"
        ));
    }

    // Admin APIs
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CollectionResponse>>> getAllCollections() {
        return ResponseEntity.ok(ApiResponse.success(
            collectionService.getAllCollections(), 
            "Lấy tất cả bộ sưu tập thành công"
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CollectionResponse>> getCollectionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
            collectionService.getCollectionById(id), 
            "Lấy chi tiết bộ sưu tập thành công"
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CollectionResponse>> createCollection(@Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
            collectionService.createCollection(request), 
            "Tạo bộ sưu tập mới thành công"
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CollectionResponse>> updateCollection(
            @PathVariable Long id, 
            @Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            collectionService.updateCollection(id, request), 
            "Cập nhật bộ sưu tập thành công"
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa bộ sưu tập thành công"));
    }
}
