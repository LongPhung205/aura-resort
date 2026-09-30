package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.VillaTypeRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaTypeResponse;
import com.phungvanlong.booking_hotel.service.VillaTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/villa-types")
@RequiredArgsConstructor
public class VillaTypeController {

    private final VillaTypeService villaTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VillaTypeResponse>>> getAllVillaTypes() {
        List<VillaTypeResponse> list = villaTypeService.getAllVillaTypes();
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh sách hạng Villa thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VillaTypeResponse>> getVillaTypeById(@PathVariable Long id) {
        VillaTypeResponse response = villaTypeService.getVillaTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin hạng Villa thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VillaTypeResponse>> createVillaType(@Valid @RequestBody VillaTypeRequest request) {
        VillaTypeResponse response = villaTypeService.createVillaType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo hạng Villa thành công"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VillaTypeResponse>> updateVillaType(
            @PathVariable Long id,
            @Valid @RequestBody VillaTypeRequest request) {
        VillaTypeResponse response = villaTypeService.updateVillaType(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật hạng Villa thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVillaType(@PathVariable Long id) {
        villaTypeService.deleteVillaType(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa hạng Villa thành công"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<VillaTypeResponse>>> searchVillaTypes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageResponse<VillaTypeResponse> response = villaTypeService.searchVillaTypes(
                checkInDate, checkOutDate, quantity, capacity, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm hạng Villa trống thành công"));
    }
}
