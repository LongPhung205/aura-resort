package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.VillaServiceRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaServiceResponse;
import com.phungvanlong.booking_hotel.entity.ExtraService;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.ExtraServiceRepository;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.repository.VillaServiceRepository;
import com.phungvanlong.booking_hotel.service.VillaServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillaServiceServiceImpl implements VillaServiceService {

    private final VillaServiceRepository villaServiceRepository;
    private final VillaRepository villaRepository;
    private final ExtraServiceRepository extraServiceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VillaServiceResponse> getByVillaId(Long villaId) {
        return villaServiceRepository.findByVillaId(villaId).stream()
                .map(VillaServiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VillaServiceResponse> getByServiceId(Long serviceId) {
        return villaServiceRepository.findByServiceId(serviceId).stream()
                .map(VillaServiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VillaServiceResponse assign(VillaServiceRequest request) {
        Villa villa = villaRepository.findById(request.getVillaId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy Villa ID: " + request.getVillaId()));
        ExtraService service = extraServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy dịch vụ ID: " + request.getServiceId()));

        // Kiểm tra đã tồn tại chưa
        var existing = villaServiceRepository.findByVillaIdAndServiceId(villa.getId(), service.getId());
        if (existing.isPresent()) {
            // Cập nhật thay vì tạo mới
            var vs = existing.get();
            if (request.getPriceOverride() != null) vs.setPriceOverride(request.getPriceOverride());
            vs.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
            if (request.getNote() != null) vs.setNote(request.getNote());
            return VillaServiceResponse.fromEntity(villaServiceRepository.save(vs));
        }

        var villaService = com.phungvanlong.booking_hotel.entity.VillaService.builder()
                .villa(villa)
                .service(service)
                .priceOverride(request.getPriceOverride())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .note(request.getNote())
                .build();
        return VillaServiceResponse.fromEntity(villaServiceRepository.save(villaService));
    }

    @Override
    @Transactional
    public List<VillaServiceResponse> bulkAssign(Long villaId, List<VillaServiceRequest> requests) {
        List<VillaServiceResponse> results = new ArrayList<>();
        for (VillaServiceRequest req : requests) {
            req.setVillaId(villaId);
            results.add(assign(req));
        }
        return results;
    }

    @Override
    @Transactional
    public VillaServiceResponse update(Long id, VillaServiceRequest request) {
        var vs = villaServiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy cấu hình dịch vụ Villa ID: " + id));
        if (request.getPriceOverride() != null) vs.setPriceOverride(request.getPriceOverride());
        if (request.getIsAvailable() != null) vs.setIsAvailable(request.getIsAvailable());
        if (request.getNote() != null) vs.setNote(request.getNote());
        return VillaServiceResponse.fromEntity(villaServiceRepository.save(vs));
    }

    @Override
    @Transactional
    public void remove(Long id) {
        if (!villaServiceRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy cấu hình dịch vụ Villa ID: " + id);
        }
        villaServiceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VillaServiceResponse toggleAvailable(Long id) {
        var vs = villaServiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy cấu hình dịch vụ Villa ID: " + id));
        vs.setIsAvailable(!Boolean.TRUE.equals(vs.getIsAvailable()));
        return VillaServiceResponse.fromEntity(villaServiceRepository.save(vs));
    }
}
