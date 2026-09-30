package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.RoomTypeRequest;
import com.phungvanlong.booking_hotel.dto.response.RoomTypeResponse;
import com.phungvanlong.booking_hotel.entity.Review;
import com.phungvanlong.booking_hotel.entity.RoomImage;
import com.phungvanlong.booking_hotel.entity.RoomType;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.ReviewRepository;
import com.phungvanlong.booking_hotel.repository.RoomImageRepository;
import com.phungvanlong.booking_hotel.repository.RoomTypeRepository;
import com.phungvanlong.booking_hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import com.phungvanlong.booking_hotel.dto.response.PageResponse;
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
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomImageRepository roomImageRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public RoomTypeResponse createRoomType(RoomTypeRequest request) {
        int ad = request.getAdults() != null ? request.getAdults() : (request.getCapacity() != null ? request.getCapacity() : 2);
        int ch = request.getChildren() != null ? request.getChildren() : 0;
        int cap = request.getCapacity() != null ? request.getCapacity() : (ad + ch);

        RoomType roomType = RoomType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .capacity(cap)
                .adults(ad)
                .children(ch)
                .bedType(request.getBedType())
                .imageUrl(request.getImageUrl())
                .build();

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<RoomImage> images = request.getImages().stream()
                    .map(url -> RoomImage.builder().imageUrl(url).roomType(roomType).build())
                    .collect(Collectors.toList());
            roomType.setImages(images);
        }

        RoomType saved = roomTypeRepository.save(roomType);
        return RoomTypeResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(this::mapToResponseWithRating)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponse getRoomTypeById(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hạng phòng"));
        return mapToResponseWithRating(roomType);
    }

    private RoomTypeResponse mapToResponseWithRating(RoomType roomType) {
        RoomTypeResponse response = RoomTypeResponse.fromEntity(roomType);
        List<Review> reviews = reviewRepository.findByRoomTypeId(roomType.getId());

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
    public RoomTypeResponse updateRoomType(Long id, RoomTypeRequest request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hạng phòng"));

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setBasePrice(request.getBasePrice());
        int ad = request.getAdults() != null ? request.getAdults() : (request.getCapacity() != null ? request.getCapacity() : (roomType.getAdults() != null ? roomType.getAdults() : 2));
        int ch = request.getChildren() != null ? request.getChildren() : (roomType.getChildren() != null ? roomType.getChildren() : 0);
        int cap = request.getCapacity() != null ? request.getCapacity() : (ad + ch);
        roomType.setAdults(ad);
        roomType.setChildren(ch);
        roomType.setCapacity(cap);
        if (request.getBedType() != null) {
            roomType.setBedType(request.getBedType());
        }
        roomType.setImageUrl(request.getImageUrl());

        // Cập nhật danh sách ảnh: clear ảnh cũ, thêm ảnh mới
        if (request.getImages() != null) {
            roomType.getImages().clear();
            List<RoomImage> newImages = request.getImages().stream()
                    .map(url -> RoomImage.builder().imageUrl(url).roomType(roomType).build())
                    .collect(Collectors.toList());
            roomType.getImages().addAll(newImages);
        }

        RoomType updated = roomTypeRepository.save(roomType);
        return RoomTypeResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy hạng phòng");
        }
        roomTypeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoomTypeResponse> searchRoomTypes(LocalDate checkInDate, LocalDate checkOutDate, 
                                                          Integer quantity, Integer capacity, 
                                                          BigDecimal minPrice, BigDecimal maxPrice, 
                                                          int page, int size) {
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new BusinessException("Ngày nhận phòng phải trước ngày trả phòng");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("basePrice").ascending());
        Page<RoomType> roomTypePage = roomTypeRepository.searchRoomTypes(
                checkInDate, checkOutDate, quantity, capacity, minPrice, maxPrice, pageable);
                
        List<RoomTypeResponse> responses = roomTypePage.getContent().stream()
                .map(this::mapToResponseWithRating)
                .collect(Collectors.toList());
                
        return PageResponse.<RoomTypeResponse>builder()
                .content(responses)
                .pageNo(roomTypePage.getNumber())
                .pageSize(roomTypePage.getSize())
                .totalElements(roomTypePage.getTotalElements())
                .totalPages(roomTypePage.getTotalPages())
                .last(roomTypePage.isLast())
                .build();
    }
}
