package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.VillaServiceRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaServiceResponse;

import java.util.List;

public interface VillaServiceService {
    List<VillaServiceResponse> getByVillaId(Long villaId);
    List<VillaServiceResponse> getByServiceId(Long serviceId);
    VillaServiceResponse assign(VillaServiceRequest request);
    List<VillaServiceResponse> bulkAssign(Long villaId, List<VillaServiceRequest> requests);
    VillaServiceResponse update(Long id, VillaServiceRequest request);
    void remove(Long id);
    VillaServiceResponse toggleAvailable(Long id);
}
