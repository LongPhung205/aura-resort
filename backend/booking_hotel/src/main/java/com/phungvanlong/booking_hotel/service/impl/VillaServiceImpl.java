package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ChildRoomRequestDto;
import com.phungvanlong.booking_hotel.dto.request.VillaBedSelectionDto;
import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.RoomStatus;
import com.phungvanlong.booking_hotel.entity.RoomType;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaImage;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import com.phungvanlong.booking_hotel.entity.VillaType;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.repository.RoomRepository;
import com.phungvanlong.booking_hotel.repository.RoomTypeRepository;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import com.phungvanlong.booking_hotel.repository.VillaTypeRepository;
import com.phungvanlong.booking_hotel.service.VillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillaServiceImpl implements VillaService {

    private final VillaRepository villaRepository;
    private final VillaTypeRepository villaTypeRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public VillaResponse createVilla(VillaRequest request) {
        if (villaRepository.findByVillaNumber(request.getVillaNumber()).isPresent()) {
            throw new BusinessException("Mã căn Villa đã tồn tại: " + request.getVillaNumber());
        }

        Integer bedroomCount = request.getBedroomCount();
        if ((bedroomCount == null || bedroomCount <= 0) && request.getBedSelections() != null && !request.getBedSelections().isEmpty()) {
            bedroomCount = request.getBedSelections().stream().mapToInt(b -> b.getQuantity() != null ? b.getQuantity() : 0).sum();
        } else if ((bedroomCount == null || bedroomCount <= 0) && request.getChildRooms() != null && !request.getChildRooms().isEmpty()) {
            bedroomCount = request.getChildRooms().size();
        }

        VillaType villaType;
        if (bedroomCount != null && bedroomCount > 0) {
            String categoryName = "Villa " + bedroomCount + " Phòng Ngủ";
            final int count = bedroomCount;
            villaType = villaTypeRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        VillaType autoType = VillaType.builder()
                                .name(categoryName)
                                .description("Biệt thự nghỉ dưỡng cao cấp " + count + " phòng ngủ")
                                .basePrice(request.getBasePrice() != null ? request.getBasePrice() : BigDecimal.valueOf(15000000))
                                .capacity(count * 2)
                                .adults(count * 2)
                                .children(1)
                                .bedType(count + " Phòng Ngủ Riêng Biệt")
                                .build();
                        return villaTypeRepository.save(autoType);
                    });
        } else {
            Long typeId = request.getVillaTypeId() != null ? request.getVillaTypeId() : request.getRoomTypeId();
            if (typeId != null) {
                villaType = villaTypeRepository.findById(typeId)
                        .orElseThrow(() -> new BusinessException("Không tìm thấy hạng Villa ID: " + typeId));
            } else {
                throw new BusinessException("Vui lòng chọn hạng Villa hoặc chỉ định số lượng phòng ngủ");
            }
        }

        String amenitiesStr = null;
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            amenitiesStr = request.getAmenities().stream()
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.joining(", "));
        }

        BigDecimal basePrice = request.getBasePrice() != null ? request.getBasePrice() : villaType.getBasePrice();

        Villa villa = Villa.builder()
                .villaNumber(request.getVillaNumber())
                .floor(request.getFloor())
                .structureType(request.getStructureType())
                .basePrice(basePrice)
                .zone(request.getZone() != null ? request.getZone() : "Khu A - Biển Đông")
                .villaType(villaType)
                .status(request.getStatus() != null ? request.getStatus() : VillaStatus.AVAILABLE)
                .ozoneStatus(request.getOzoneStatus() != null ? request.getOzoneStatus() : "STERILIZED")
                .amenities(amenitiesStr)
                .bedroomCount(bedroomCount)
                .lastCleanedAt(LocalDateTime.now())
                .build();

        populateChildRooms(villa, request);

        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            villa.getImages().add(VillaImage.builder()
                    .imageUrl(request.getImageUrl().trim())
                    .isPrimary(true)
                    .villa(villa)
                    .build());
        }
        if (request.getImages() != null) {
            for (String img : request.getImages()) {
                if (img != null && !img.trim().isEmpty() && !img.equals(request.getImageUrl())) {
                    villa.getImages().add(VillaImage.builder()
                            .imageUrl(img.trim())
                            .isPrimary(villa.getImages().isEmpty())
                            .villa(villa)
                            .build());
                }
            }
        }

        Villa saved = villaRepository.save(villa);
        return VillaResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public VillaResponse updateVilla(Long id, VillaRequest request) {
        Villa villa = villaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Villa ID: " + id));

        if (request.getVillaNumber() != null && !villa.getVillaNumber().equalsIgnoreCase(request.getVillaNumber()) &&
                villaRepository.findByVillaNumber(request.getVillaNumber()).isPresent()) {
            throw new BusinessException("Mã căn Villa mới đã tồn tại: " + request.getVillaNumber());
        }

        Integer bedroomCount = request.getBedroomCount();
        if ((bedroomCount == null || bedroomCount <= 0) && request.getBedSelections() != null && !request.getBedSelections().isEmpty()) {
            bedroomCount = request.getBedSelections().stream().mapToInt(b -> b.getQuantity() != null ? b.getQuantity() : 0).sum();
        } else if ((bedroomCount == null || bedroomCount <= 0) && request.getChildRooms() != null && !request.getChildRooms().isEmpty()) {
            bedroomCount = request.getChildRooms().size();
        }

        if (bedroomCount != null && bedroomCount > 0) {
            villa.setBedroomCount(bedroomCount);
            String categoryName = "Villa " + bedroomCount + " Phòng Ngủ";
            final int count = bedroomCount;
            VillaType villaType = villaTypeRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        VillaType autoType = VillaType.builder()
                                .name(categoryName)
                                .description("Biệt thự nghỉ dưỡng cao cấp " + count + " phòng ngủ")
                                .basePrice(request.getBasePrice() != null ? request.getBasePrice() : (villa.getBasePrice() != null ? villa.getBasePrice() : BigDecimal.valueOf(15000000)))
                                .capacity(count * 2)
                                .adults(count * 2)
                                .children(1)
                                .bedType(count + " Phòng Ngủ Riêng Biệt")
                                .build();
                        return villaTypeRepository.save(autoType);
                    });
            villa.setVillaType(villaType);
        } else {
            Long typeId = request.getVillaTypeId() != null ? request.getVillaTypeId() : request.getRoomTypeId();
            if (typeId != null) {
                VillaType villaType = villaTypeRepository.findById(typeId)
                        .orElseThrow(() -> new BusinessException("Không tìm thấy hạng Villa ID: " + typeId));
                villa.setVillaType(villaType);
            }
        }

        if (request.getVillaNumber() != null) {
            villa.setVillaNumber(request.getVillaNumber());
        }
        if (request.getFloor() != null) {
            villa.setFloor(request.getFloor());
        }
        if (request.getStructureType() != null) {
            villa.setStructureType(request.getStructureType());
        }
        if (request.getBasePrice() != null) {
            villa.setBasePrice(request.getBasePrice());
        }
        if (request.getZone() != null) {
            villa.setZone(request.getZone());
        }
        if (request.getStatus() != null) {
            villa.setStatus(request.getStatus());
        }
        if (request.getOzoneStatus() != null) {
            villa.setOzoneStatus(request.getOzoneStatus());
        }
        if (request.getAmenities() != null) {
            villa.setAmenities(request.getAmenities().stream()
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.joining(", ")));
        }

        // Update child rooms if provided
        if ((request.getBedSelections() != null && !request.getBedSelections().isEmpty()) ||
                (request.getChildRooms() != null && !request.getChildRooms().isEmpty())) {
            List<Room> oldRooms = new java.util.ArrayList<>(villa.getRooms());
            villa.getRooms().clear();
            roomRepository.deleteAll(oldRooms);
            roomRepository.flush();
            populateChildRooms(villa, request);
        }

        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            villa.getImages().clear();
            villa.getImages().add(VillaImage.builder()
                    .imageUrl(request.getImageUrl().trim())
                    .isPrimary(true)
                    .villa(villa)
                    .build());
        }
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            villa.getImages().clear();
            boolean first = true;
            for (String img : request.getImages()) {
                if (img != null && !img.trim().isEmpty()) {
                    villa.getImages().add(VillaImage.builder()
                            .imageUrl(img.trim())
                            .isPrimary(first)
                            .villa(villa)
                            .build());
                    first = false;
                }
            }
        }

        Villa updated = villaRepository.save(villa);
        return VillaResponse.fromEntity(updated);
    }

    private void populateChildRooms(Villa villa, VillaRequest request) {
        if (request.getChildRooms() != null && !request.getChildRooms().isEmpty()) {
            int roomIndex = 1;
            for (ChildRoomRequestDto crDto : request.getChildRooms()) {
                RoomType roomType = null;
                if (crDto.getRoomTypeId() != null) {
                    roomType = roomTypeRepository.findById(crDto.getRoomTypeId()).orElse(null);
                }
                String rNum = crDto.getRoomNumber() != null && !crDto.getRoomNumber().trim().isEmpty()
                        ? crDto.getRoomNumber().trim()
                        : villa.getVillaNumber() + "-P" + roomIndex;
                String rName = crDto.getName() != null && !crDto.getName().trim().isEmpty()
                        ? crDto.getName().trim()
                        : "Phòng Ngủ " + roomIndex + (roomType != null ? " (" + roomType.getName() + ")" : "");
                Integer rFloor = crDto.getFloor() != null
                        ? crDto.getFloor()
                        : Math.min(roomIndex, villa.getFloor() != null ? villa.getFloor() : 1);

                Room childRoom = Room.builder()
                        .roomNumber(rNum)
                        .name(rName)
                        .description(crDto.getDescription())
                        .floor(rFloor)
                        .roomType(roomType)
                        .zone(villa.getZone())
                        .status(RoomStatus.AVAILABLE)
                        .villa(villa)
                        .build();
                villa.getRooms().add(childRoom);
                roomIndex++;
            }
        } else if (request.getBedSelections() != null && !request.getBedSelections().isEmpty()) {
            int roomIndex = 1;
            for (VillaBedSelectionDto selection : request.getBedSelections()) {
                if (selection.getRoomTypeId() == null || selection.getQuantity() == null || selection.getQuantity() <= 0) {
                    continue;
                }
                RoomType roomType = roomTypeRepository.findById(selection.getRoomTypeId())
                        .orElseThrow(() -> new BusinessException("Không tìm thấy loại giường / phòng ID: " + selection.getRoomTypeId()));
                for (int i = 0; i < selection.getQuantity(); i++) {
                    Room childRoom = Room.builder()
                            .roomNumber(villa.getVillaNumber() + "-P" + roomIndex)
                            .name("Phòng Ngủ " + roomIndex + " (" + roomType.getName() + ")")
                            .floor(Math.min(roomIndex, villa.getFloor() != null ? villa.getFloor() : 1))
                            .roomType(roomType)
                            .zone(villa.getZone())
                            .status(RoomStatus.AVAILABLE)
                            .villa(villa)
                            .build();
                    villa.getRooms().add(childRoom);
                    roomIndex++;
                }
            }
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Vui lòng chọn tệp hình ảnh hợp lệ!");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueName = "villa_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            // Save to frontend public assets directory
            Path frontendUploadDir = Paths.get("d:/booking_hotel/frontend/public/assets/images/uploads");
            if (!Files.exists(frontendUploadDir)) {
                Files.createDirectories(frontendUploadDir);
            }
            Path targetPath = frontendUploadDir.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/assets/images/uploads/" + uniqueName;
        } catch (Exception e) {
            try {
                String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
                return "data:" + contentType + ";base64," + base64;
            } catch (Exception ex) {
                throw new BusinessException("Không thể xử lý tệp ảnh tải lên: " + ex.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VillaResponse getVillaById(Long id) {
        Villa villa = villaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Villa ID: " + id));
        return VillaResponse.fromEntity(villa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VillaResponse> getAllVillas(Long villaTypeId, VillaStatus status, String zone) {
        List<Villa> list = villaRepository.findByFilters(villaTypeId, status, zone);
        return list.stream().map(VillaResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VillaResponse> getVillasByVillaTypeId(Long villaTypeId) {
        return villaRepository.findByVillaTypeId(villaTypeId).stream()
                .map(VillaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VillaResponse updateVillaStatus(Long id, VillaStatus status) {
        Villa villa = villaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Villa ID: " + id));
        villa.setStatus(status);
        if (status == VillaStatus.AVAILABLE) {
            villa.setLastCleanedAt(LocalDateTime.now());
            villa.setOzoneStatus("STERILIZED");
        }
        Villa updated = villaRepository.save(villa);
        return VillaResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteVilla(Long id) {
        Villa villa = villaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Villa ID: " + id));
        villaRepository.delete(villa);
    }
}
