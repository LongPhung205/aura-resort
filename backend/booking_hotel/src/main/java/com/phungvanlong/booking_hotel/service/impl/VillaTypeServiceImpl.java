package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.VillaTypeRequest;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
import com.phungvanlong.booking_hotel.dto.response.VillaTypeResponse;
import com.phungvanlong.booking_hotel.entity.Review;
import com.phungvanlong.booking_hotel.entity.VillaType;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.ReviewRepository;
import com.phungvanlong.booking_hotel.repository.VillaTypeRepository;
import com.phungvanlong.booking_hotel.service.VillaTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillaTypeServiceImpl implements VillaTypeService {

    private final VillaTypeRepository villaTypeRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public VillaTypeResponse createVillaType(VillaTypeRequest request) {
        if (villaTypeRepository.existsByName(request.getName())) {
            throw new BusinessException("Tên hạng Villa đã tồn tại: " + request.getName());
        }

        int ad = request.getAdults() != null ? request.getAdults() : (request.getCapacity() != null ? request.getCapacity() : 2);
        int ch = request.getChildren() != null ? request.getChildren() : 0;
        int cap = request.getCapacity() != null ? request.getCapacity() : (ad + ch);

        VillaType villaType = VillaType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .dynamicPrice(request.getDynamicPrice() != null ? request.getDynamicPrice() : request.getBasePrice())
                .capacity(cap)
                .adults(ad)
                .children(ch)
                .bedType(request.getBedType())
                .imageUrl(request.getImageUrl())
                .build();

        VillaType saved = villaTypeRepository.save(villaType);
        return VillaTypeResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VillaTypeResponse> getAllVillaTypes() {
        return villaTypeRepository.findAll().stream()
                .map(this::mapToResponseWithRating)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VillaTypeResponse getVillaTypeById(Long id) {
        VillaType villaType = villaTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hạng Villa ID: " + id));
        return mapToResponseWithRating(villaType);
    }

    private VillaTypeResponse mapToResponseWithRating(VillaType villaType) {
        VillaTypeResponse response = VillaTypeResponse.fromEntity(villaType);
        List<Review> reviews = reviewRepository.findByVillaTypeId(villaType.getId());

        response.setTotalReviews(reviews.size());
        if (reviews.isEmpty()) {
            response.setAverageRating(0.0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            response.setAverageRating(Math.round(avg * 10.0) / 10.0);
        }
        return response;
    }

    @Override
    @Transactional
    public VillaTypeResponse updateVillaType(Long id, VillaTypeRequest request) {
        VillaType villaType = villaTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hạng Villa ID: " + id));

        villaType.setName(request.getName());
        villaType.setDescription(request.getDescription());
        villaType.setBasePrice(request.getBasePrice());
        if (request.getDynamicPrice() != null) {
            villaType.setDynamicPrice(request.getDynamicPrice());
        }
        int ad = request.getAdults() != null ? request.getAdults() : (request.getCapacity() != null ? request.getCapacity() : (villaType.getAdults() != null ? villaType.getAdults() : 2));
        int ch = request.getChildren() != null ? request.getChildren() : (villaType.getChildren() != null ? villaType.getChildren() : 0);
        int cap = request.getCapacity() != null ? request.getCapacity() : (ad + ch);
        villaType.setAdults(ad);
        villaType.setChildren(ch);
        villaType.setCapacity(cap);
        if (request.getBedType() != null) {
            villaType.setBedType(request.getBedType());
        }
        villaType.setImageUrl(request.getImageUrl());

        VillaType updated = villaTypeRepository.save(villaType);
        return VillaTypeResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteVillaType(Long id) {
        if (!villaTypeRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy hạng Villa ID: " + id);
        }
        villaTypeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VillaTypeResponse> searchVillaTypes(
            LocalDate checkInDate, LocalDate checkOutDate, 
            Integer quantity, Integer capacity, 
            BigDecimal minPrice, BigDecimal maxPrice, 
            int page, int size) {
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new BusinessException("Ngày nhận phòng/villa phải trước ngày trả");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("basePrice").ascending());
        Page<VillaType> pageResult = villaTypeRepository.searchVillaTypes(
                checkInDate, checkOutDate, quantity, capacity, minPrice, maxPrice, pageable);

        List<VillaTypeResponse> list = pageResult.getContent().stream()
                .map(this::mapToResponseWithRating)
                .collect(Collectors.toList());

        return PageResponse.<VillaTypeResponse>builder()
                .content(list)
                .pageNo(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }
}
