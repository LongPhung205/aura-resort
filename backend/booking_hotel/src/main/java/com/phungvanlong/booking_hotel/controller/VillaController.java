package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import com.phungvanlong.booking_hotel.service.VillaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/villas", "/rooms"})
@RequiredArgsConstructor
public class VillaController {

    private final VillaService villaService;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VillaResponse>>> getVillas(
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) VillaStatus status,
            @RequestParam(required = false) String zone) {
        List<VillaResponse> list = villaService.getAllVillas(typeId, status, zone);
        return ResponseEntity.ok(ApiResponse.success(list, "Lấy danh sách Villa thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VillaResponse>> getVillaById(@PathVariable Long id) {
        VillaResponse villa = villaService.getVillaById(id);
        return ResponseEntity.ok(ApiResponse.success(villa, "Lấy thông tin chi tiết Villa thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VillaResponse>> createVilla(@Valid @RequestBody VillaRequest request) {
        VillaResponse villa = villaService.createVilla(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(villa, "Tạo căn Villa mới thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VillaResponse>> updateVilla(
            @PathVariable Long id,
            @Valid @RequestBody VillaRequest request) {
        VillaResponse villa = villaService.updateVilla(id, request);
        return ResponseEntity.ok(ApiResponse.success(villa, "Cập nhật thông tin Villa thành công"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<VillaResponse>> updateVillaStatus(
            @PathVariable Long id,
            @RequestParam VillaStatus status) {
        VillaResponse villa = villaService.updateVillaStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(villa, "Cập nhật trạng thái Villa thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVilla(@PathVariable Long id) {
        villaService.deleteVilla(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa căn Villa thành công"));
    }

    @PostMapping(value = "/upload-image", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        String url = villaService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.success(url, "Tải ảnh lên thành công"));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable String filename) {
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir).resolve(filename).normalize();
            if (!java.nio.file.Files.exists(filePath) || !java.nio.file.Files.isReadable(filePath)) {
                return ResponseEntity.notFound().build();
            }

            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            String contentType = java.nio.file.Files.probeContentType(filePath);
            if (contentType == null) {
                if (filename.toLowerCase().endsWith(".png")) contentType = "image/png";
                else if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";
                else contentType = "image/jpeg";
            }

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(org.springframework.http.HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                    .body(resource);
        } catch (Exception e) {
            log.warn("Lỗi serve ảnh {}: {}", filename, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

