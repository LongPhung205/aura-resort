package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ZoneRequest;
import com.phungvanlong.booking_hotel.dto.response.ZoneDetailResponse;
import com.phungvanlong.booking_hotel.dto.response.ZoneResponse;

import java.util.List;

public interface AdminZoneService {

    List<ZoneResponse> getAllZones();

    List<ZoneResponse> getActiveZones();

    ZoneResponse getZoneById(Long id);

    ZoneDetailResponse getZoneDetailBySlug(String slug);

    ZoneResponse createZone(ZoneRequest request);

    ZoneResponse updateZone(Long id, ZoneRequest request);

    void deleteZone(Long id);
}
