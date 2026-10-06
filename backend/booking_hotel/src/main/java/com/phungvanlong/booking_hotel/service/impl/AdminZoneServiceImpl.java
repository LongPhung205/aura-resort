package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ZoneRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.dto.response.ZoneDetailResponse;
import com.phungvanlong.booking_hotel.dto.response.ZoneResponse;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.Zone;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.repository.ZoneRepository;
import com.phungvanlong.booking_hotel.service.AdminZoneService;
import com.phungvanlong.booking_hotel.mapper.VillaMapper;
import com.phungvanlong.booking_hotel.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminZoneServiceImpl implements AdminZoneService {

    private final ZoneRepository zoneRepository;
    private final VillaRepository villaRepository;
    private final VillaMapper villaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getAllZones() {
        return zoneRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(z -> {
                    long count = villaRepository.countByZoneId(z.getId());
                    return ZoneResponse.fromEntity(z, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getActiveZones() {
        return zoneRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(z -> {
                    long count = villaRepository.countByZoneId(z.getId());
                    return ZoneResponse.fromEntity(z, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneResponse getZoneById(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân khu ID: " + id));
        long count = villaRepository.countByZoneId(zone.getId());
        return ZoneResponse.fromEntity(zone, count);
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneDetailResponse getZoneDetailBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new ResourceNotFoundException("Slug phân khu không hợp lệ!");
        }

        String cleanSlug = SlugUtils.toSlug(slug);
        Zone zone = zoneRepository.findBySlugIgnoreCase(cleanSlug)
                .or(() -> zoneRepository.findBySlugIgnoreCase(slug))
                .or(() -> {
                    // Fallback: match by name or partial slug (e.g. villa-ngoc-trai -> ngoc trai)
                    String normalizedName = cleanSlug.replace("villa-", "").replace("-", " ");
                    return zoneRepository.findByNameIgnoreCase(normalizedName);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân khu với đường dẫn: " + slug));

        List<Villa> villas = villaRepository.findByZoneId(zone.getId());
        List<VillaResponse> villaResponses = villas.stream()
                .map(villaMapper::toResponse)
                .collect(Collectors.toList());

        return ZoneDetailResponse.builder()
                .zone(ZoneResponse.fromEntity(zone, villas.size()))
                .villas(villaResponses)
                .build();
    }

    @Override
    @Transactional
    public ZoneResponse createZone(ZoneRequest request) {
        String trimmedName = request.getName().trim();
        if (zoneRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new BusinessException("Phân khu có tên \"" + trimmedName + "\" đã tồn tại!");
        }

        String matchKey = trimmedName.toLowerCase();
        String tag = request.getTag() != null && !request.getTag().isBlank()
                ? request.getTag().trim().toUpperCase()
                : trimmedName.toUpperCase();
        String icon = request.getIcon() != null && !request.getIcon().isBlank()
                ? request.getIcon().trim()
                : "holiday_village";
        String badgeClass = request.getBadgeClass() != null && !request.getBadgeClass().isBlank()
                ? request.getBadgeClass().trim()
                : "bg-sky-50 text-sky-700 border-sky-200";

        // Generate or sanitize slug
        String rawSlug = request.getSlug() != null && !request.getSlug().isBlank()
                ? request.getSlug()
                : "villa-" + trimmedName;
        String slug = SlugUtils.toSlug(rawSlug);
        if (zoneRepository.existsBySlugIgnoreCase(slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 1000;
        }

        int displayOrder = request.getDisplayOrder() != null
                ? request.getDisplayOrder()
                : (int) zoneRepository.count() + 1;

        Zone zone = Zone.builder()
                .name(trimmedName)
                .matchKey(matchKey)
                .tag(tag)
                .icon(icon)
                .badgeClass(badgeClass)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .slug(slug)
                .bannerUrl(request.getBannerUrl() != null && !request.getBannerUrl().isBlank() ? request.getBannerUrl().trim() : null)
                .highlights(request.getHighlights() != null ? request.getHighlights().trim() : null)
                .displayOrder(displayOrder)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Zone saved = zoneRepository.save(zone);
        return ZoneResponse.fromEntity(saved, 0);
    }

    @Override
    @Transactional
    public ZoneResponse updateZone(Long id, ZoneRequest request) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân khu ID: " + id));

        String trimmedName = request.getName().trim();
        if (zoneRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new BusinessException("Tên phân khu \"" + trimmedName + "\" đã được sử dụng bởi phân khu khác!");
        }

        zone.setName(trimmedName);
        zone.setMatchKey(trimmedName.toLowerCase());

        if (request.getTag() != null && !request.getTag().isBlank()) {
            zone.setTag(request.getTag().trim().toUpperCase());
        }
        if (request.getIcon() != null && !request.getIcon().isBlank()) {
            zone.setIcon(request.getIcon().trim());
        }
        if (request.getBadgeClass() != null && !request.getBadgeClass().isBlank()) {
            zone.setBadgeClass(request.getBadgeClass().trim());
        }
        if (request.getDescription() != null) {
            zone.setDescription(request.getDescription().trim());
        }
        if (request.getIsActive() != null) {
            zone.setIsActive(request.getIsActive());
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String sanitizedSlug = SlugUtils.toSlug(request.getSlug());
            if (zoneRepository.existsBySlugIgnoreCaseAndIdNot(sanitizedSlug, id)) {
                throw new BusinessException("Đường dẫn slug \"" + sanitizedSlug + "\" đã được sử dụng!");
            }
            zone.setSlug(sanitizedSlug);
        } else if (zone.getSlug() == null || zone.getSlug().isBlank()) {
            zone.setSlug(SlugUtils.toSlug("villa-" + trimmedName));
        }

        if (request.getBannerUrl() != null) {
            zone.setBannerUrl(request.getBannerUrl().trim());
        }
        if (request.getHighlights() != null) {
            zone.setHighlights(request.getHighlights().trim());
        }
        if (request.getDisplayOrder() != null) {
            zone.setDisplayOrder(request.getDisplayOrder());
        }

        Zone updated = zoneRepository.save(zone);
        long count = villaRepository.countByZoneId(updated.getId());
        return ZoneResponse.fromEntity(updated, count);
    }

    @Override
    @Transactional
    public void deleteZone(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân khu ID: " + id));

        long count = villaRepository.countByZoneId(id);
        if (count > 0) {
            throw new BusinessException("Không thể xóa phân khu \"" + zone.getName() + "\" vì đang có " + count + " biệt thự trực thuộc!");
        }

        zoneRepository.delete(zone);
    }
}
