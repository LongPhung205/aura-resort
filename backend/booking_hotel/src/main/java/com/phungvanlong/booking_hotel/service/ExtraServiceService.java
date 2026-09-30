package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ExtraServiceRequest;
import com.phungvanlong.booking_hotel.dto.response.ExtraServiceResponse;

import java.util.List;

public interface ExtraServiceService {
    ExtraServiceResponse createService(ExtraServiceRequest request);
    ExtraServiceResponse updateService(Long id, ExtraServiceRequest request);
    void deleteService(Long id);
    List<ExtraServiceResponse> getAllServices();
    ExtraServiceResponse toggleActive(Long id);
}
