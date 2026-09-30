package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.VillaStatus;

import java.util.List;

public interface VillaService {
    VillaResponse createVilla(VillaRequest request);
    VillaResponse updateVilla(Long id, VillaRequest request);
    VillaResponse getVillaById(Long id);
    List<VillaResponse> getAllVillas(Long villaTypeId, VillaStatus status, String zone);
    List<VillaResponse> getVillasByVillaTypeId(Long villaTypeId);
    VillaResponse updateVillaStatus(Long id, VillaStatus status);
    void deleteVilla(Long id);
    String uploadImage(org.springframework.web.multipart.MultipartFile file);
}
