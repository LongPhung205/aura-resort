package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ComboPackageRequest;
import com.phungvanlong.booking_hotel.dto.response.ComboPackageResponse;

import java.util.List;

public interface ComboPackageService {
    ComboPackageResponse create(ComboPackageRequest request);
    ComboPackageResponse update(Long id, ComboPackageRequest request);
    void delete(Long id);
    List<ComboPackageResponse> getAll();
    List<ComboPackageResponse> getActive();
}
