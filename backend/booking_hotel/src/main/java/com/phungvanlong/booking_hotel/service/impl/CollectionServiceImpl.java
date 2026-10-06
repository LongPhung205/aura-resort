package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.CollectionRequest;
import com.phungvanlong.booking_hotel.dto.response.CollectionResponse;
import com.phungvanlong.booking_hotel.entity.Collection;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.CollectionRepository;
import com.phungvanlong.booking_hotel.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;

    @Override
    public List<CollectionResponse> getAllCollections() {
        return collectionRepository.findAll().stream()
                .map(CollectionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollectionResponse> getActiveCollections() {
        return collectionRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(CollectionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public CollectionResponse getCollectionById(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ sưu tập với id: " + id));
        return CollectionResponse.fromEntity(collection);
    }

    @Override
    public CollectionResponse getCollectionBySlug(String slug) {
        Collection collection = collectionRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ sưu tập với slug: " + slug));
        return CollectionResponse.fromEntity(collection);
    }

    @Override
    @Transactional
    public CollectionResponse createCollection(CollectionRequest request) {
        if (collectionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tên bộ sưu tập đã tồn tại");
        }
        
        String slug = request.getSlug() != null && !request.getSlug().isEmpty() 
            ? request.getSlug() 
            : generateSlug(request.getName());
            
        if (collectionRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Collection collection = Collection.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 1)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return CollectionResponse.fromEntity(collectionRepository.save(collection));
    }

    @Override
    @Transactional
    public CollectionResponse updateCollection(Long id, CollectionRequest request) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ sưu tập với id: " + id));

        if (!collection.getName().equals(request.getName()) && collectionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tên bộ sưu tập đã tồn tại");
        }

        collection.setName(request.getName());
        collection.setDescription(request.getDescription());
        collection.setImageUrl(request.getImageUrl());
        
        if (request.getSlug() != null && !request.getSlug().isEmpty()) {
            if (!collection.getSlug().equals(request.getSlug()) && collectionRepository.existsBySlug(request.getSlug())) {
                throw new IllegalArgumentException("Slug đã tồn tại");
            }
            collection.setSlug(request.getSlug());
        }
        
        if (request.getDisplayOrder() != null) {
            collection.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsActive() != null) {
            collection.setIsActive(request.getIsActive());
        }

        return CollectionResponse.fromEntity(collectionRepository.save(collection));
    }

    @Override
    @Transactional
    public void deleteCollection(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ sưu tập với id: " + id));
        collectionRepository.delete(collection);
    }
    
    private String generateSlug(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[íìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
