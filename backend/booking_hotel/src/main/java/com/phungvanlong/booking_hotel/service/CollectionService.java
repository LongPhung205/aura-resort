package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.CollectionRequest;
import com.phungvanlong.booking_hotel.dto.response.CollectionResponse;

import java.util.List;

public interface CollectionService {
    List<CollectionResponse> getAllCollections();
    List<CollectionResponse> getActiveCollections();
    CollectionResponse getCollectionById(Long id);
    CollectionResponse getCollectionBySlug(String slug);
    CollectionResponse createCollection(CollectionRequest request);
    CollectionResponse updateCollection(Long id, CollectionRequest request);
    void deleteCollection(Long id);
}
