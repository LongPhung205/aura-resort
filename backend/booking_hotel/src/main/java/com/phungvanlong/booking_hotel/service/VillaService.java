package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VillaService {
    VillaResponse createVilla(VillaRequest request);
    VillaResponse updateVilla(Long id, VillaRequest request);
    VillaResponse getVillaById(Long id);
    List<VillaResponse> getAllVillas(Long villaTypeId, VillaStatus status, String zone);
    PageResponse<VillaResponse> getVillasPaged(Long villaTypeId, VillaStatus status, String zone, Pageable pageable);
    List<VillaResponse> getVillasByVillaTypeId(Long villaTypeId);
    VillaResponse updateVillaStatus(Long id, VillaStatus status);
    void deleteVilla(Long id);
    List<VillaResponse> searchAvailableVillas(String zoneName, Integer minAdults, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate);
    String uploadImage(org.springframework.web.multipart.MultipartFile file);
}
