package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ComboPackageRequest;
import com.phungvanlong.booking_hotel.dto.response.ComboPackageResponse;
import com.phungvanlong.booking_hotel.entity.ComboPackage;
import com.phungvanlong.booking_hotel.entity.ExtraService;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.ComboPackageRepository;
import com.phungvanlong.booking_hotel.repository.ExtraServiceRepository;
import com.phungvanlong.booking_hotel.service.ComboPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ComboPackageServiceImpl implements ComboPackageService {

    private final ComboPackageRepository comboPackageRepository;
    private final ExtraServiceRepository extraServiceRepository;

    @Override
    public ComboPackageResponse create(ComboPackageRequest request) {
        ComboPackage combo = ComboPackage.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .status(request.getStatus() != null ? request.getStatus() : "PUBLISH")
                .build();
                
        if (request.getExtraServiceIds() != null && !request.getExtraServiceIds().isEmpty()) {
            List<ExtraService> services = extraServiceRepository.findAllById(request.getExtraServiceIds());
            combo.setExtraServices(services);
        }
                
        return ComboPackageResponse.fromEntity(comboPackageRepository.save(combo));
    }

    @Override
    public ComboPackageResponse update(Long id, ComboPackageRequest request) {
        ComboPackage combo = comboPackageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy gói combo"));

        combo.setName(request.getName());
        combo.setDescription(request.getDescription());
        combo.setImageUrl(request.getImageUrl());
        combo.setPrice(request.getPrice());
        if (request.getStatus() != null) combo.setStatus(request.getStatus());
        
        if (request.getExtraServiceIds() != null) {
            List<ExtraService> services = extraServiceRepository.findAllById(request.getExtraServiceIds());
            combo.setExtraServices(services);
        } else {
            combo.getExtraServices().clear();
        }

        return ComboPackageResponse.fromEntity(comboPackageRepository.save(combo));
    }

    @Override
    public void delete(Long id) {
        comboPackageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy gói combo"));
        comboPackageRepository.deleteById(id);
    }

    @Override
    public List<ComboPackageResponse> getAll() {
        return comboPackageRepository.findAll().stream()
                .map(ComboPackageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComboPackageResponse> getActive() {
        return comboPackageRepository.findByStatusOrderByCreatedAtDesc("PUBLISH").stream()
                .map(ComboPackageResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
