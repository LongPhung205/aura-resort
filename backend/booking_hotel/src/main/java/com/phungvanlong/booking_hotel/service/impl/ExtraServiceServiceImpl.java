package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ExtraServiceRequest;
import com.phungvanlong.booking_hotel.dto.response.ExtraServiceResponse;
import com.phungvanlong.booking_hotel.entity.ExtraService;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.ExtraServiceRepository;
import com.phungvanlong.booking_hotel.repository.VillaServiceRepository;
import com.phungvanlong.booking_hotel.service.ExtraServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtraServiceServiceImpl implements ExtraServiceService {

    private final ExtraServiceRepository extraServiceRepository;
    private final VillaServiceRepository villaServiceRepository;

    @Override
    @Transactional
    public ExtraServiceResponse createService(ExtraServiceRequest request) {
        ExtraService service = ExtraService.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .type(request.getType() != null ? request.getType() : "OTHER")
                .unit(request.getUnit() != null ? request.getUnit() : "lần")
                .icon(request.getIcon())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        ExtraService saved = extraServiceRepository.save(service);
        return ExtraServiceResponse.fromEntity(saved, 0);
    }

    @Override
    @Transactional
    public ExtraServiceResponse updateService(Long id, ExtraServiceRequest request) {
        ExtraService service = extraServiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy dịch vụ ID: " + id));
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        if (request.getType() != null) service.setType(request.getType());
        if (request.getUnit() != null) service.setUnit(request.getUnit());
        if (request.getIcon() != null) service.setIcon(request.getIcon());
        service.setImageUrl(request.getImageUrl());
        if (request.getIsActive() != null) service.setIsActive(request.getIsActive());
        ExtraService saved = extraServiceRepository.save(service);
        int count = villaServiceRepository.countByServiceIdAndIsAvailableTrue(id);
        return ExtraServiceResponse.fromEntity(saved, count);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        ExtraService service = extraServiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy dịch vụ ID: " + id));
        extraServiceRepository.delete(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExtraServiceResponse> getAllServices() {
        return extraServiceRepository.findAll().stream()
                .map(s -> {
                    int count = villaServiceRepository.countByServiceIdAndIsAvailableTrue(s.getId());
                    return ExtraServiceResponse.fromEntity(s, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExtraServiceResponse toggleActive(Long id) {
        ExtraService service = extraServiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy dịch vụ ID: " + id));
        service.setIsActive(!Boolean.TRUE.equals(service.getIsActive()));
        ExtraService saved = extraServiceRepository.save(service);
        int count = villaServiceRepository.countByServiceIdAndIsAvailableTrue(id);
        return ExtraServiceResponse.fromEntity(saved, count);
    }
}
