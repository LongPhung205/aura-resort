package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ServiceDispatchRequest;
import com.phungvanlong.booking_hotel.dto.response.ServiceDispatchResponse;

import java.util.List;

public interface AdminServiceDispatchService {
    List<ServiceDispatchResponse> getAllDispatches(String status);
    ServiceDispatchResponse createDispatch(ServiceDispatchRequest request);
    ServiceDispatchResponse updateStatus(Long id, String status);
}
