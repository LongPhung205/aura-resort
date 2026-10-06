package com.phungvanlong.booking_hotel.controller;

import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.service.VillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/villas")
@RequiredArgsConstructor
public class PublicVillaController {

    private final VillaService villaService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VillaResponse>>> searchAvailableVillas(
            @RequestParam(required = false, defaultValue = "all") String zone,
            @RequestParam(required = false) Integer adults,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        
        List<VillaResponse> villas = villaService.searchAvailableVillas(zone, adults, checkInDate, checkOutDate);
        return ResponseEntity.ok(ApiResponse.success(villas, "Tìm kiếm Biệt thự trống thành công"));
    }
}
