package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.ChildRoomRequestDto;
import com.phungvanlong.booking_hotel.dto.request.VillaBedSelectionDto;
import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import com.phungvanlong.booking_hotel.entity.VillaType;
import com.phungvanlong.booking_hotel.repository.RoomTypeRepository;
import com.phungvanlong.booking_hotel.repository.VillaTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class VillaServiceRedesignTest {

    @Autowired
    private VillaService villaService;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private VillaTypeRepository villaTypeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        try {
            jdbcTemplate.execute("INSERT IGNORE INTO room_types (id, name, base_price, capacity, adults, children, bed_type) VALUES (1, 'Single Bed', 1000000, 1, 1, 0, '1 Single Bed')");
            jdbcTemplate.execute("INSERT IGNORE INTO room_types (id, name, base_price, capacity, adults, children, bed_type) VALUES (3, 'King Size Bed', 2500000, 3, 2, 1, '1 King Bed')");
        } catch (Exception ignored) {}
    }

    @Test
    void testCreateVillaWithBedSelectionsAutoNaming() {
        VillaRequest request = VillaRequest.builder()
                .villaNumber("TEST-VILLA-999")
                .floor(2)
                .structureType("2 Tầng")
                .basePrice(BigDecimal.valueOf(28000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .amenities(List.of("Khử trùng Ozon định kỳ", "Quản gia riêng Lead Butler 24/7", "Hồ bơi vô cực"))
                .bedroomCount(3)
                .bedSelections(List.of(
                        VillaBedSelectionDto.builder().roomTypeId(1L).quantity(1).build(), // Single Bed
                        VillaBedSelectionDto.builder().roomTypeId(3L).quantity(2).build()  // King Size Bed x2
                ))
                .build();

        VillaResponse response = villaService.createVilla(request);
        assertNotNull(response);
        assertEquals("TEST-VILLA-999", response.getVillaNumber());
        assertEquals("2 Tầng", response.getStructureType());
        assertEquals("Ngọc Trai", response.getZone());
        assertNotNull(response.getVillaTypeName());
        assertEquals(3, response.getBedroomCount());
        assertEquals(3, response.getRooms().size());
        assertEquals(3, response.getAmenities().size());
        assertTrue(response.getTotalAdults() >= 4);
    }

    @Test
    void testCreateVillaWithChildRoomsDirectly() {
        VillaRequest request = VillaRequest.builder()
                .villaNumber("TEST-VILLA-888")
                .floor(1)
                .structureType("1 Tầng")
                .basePrice(BigDecimal.valueOf(18000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .amenities(List.of("Hồ bơi riêng", "BBQ ngoài trời"))
                .bedroomCount(2)
                .childRooms(List.of(
                        ChildRoomRequestDto.builder()
                                .roomNumber("TEST-VILLA-888-P1")
                                .name("Master Bedroom View Biển")
                                .floor(1)
                                .roomTypeId(3L)
                                .description("Phòng master có bồn tắm nằm")
                                .build(),
                        ChildRoomRequestDto.builder()
                                .roomNumber("TEST-VILLA-888-P2")
                                .name("Deluxe Twin Bedroom")
                                .floor(1)
                                .roomTypeId(1L)
                                .description("Phòng 2 giường đơn")
                                .build()
                ))
                .build();

        VillaResponse response = villaService.createVilla(request);
        assertNotNull(response);
        assertEquals("TEST-VILLA-888", response.getVillaNumber());
        assertNotNull(response.getVillaTypeName());
        assertEquals(2, response.getBedroomCount());
        assertEquals(2, response.getRooms().size());
        assertEquals("Master Bedroom View Biển", response.getRooms().get(0).getName());
        assertEquals("Deluxe Twin Bedroom", response.getRooms().get(1).getName());
    }

    @Test
    void testUpdateVillaWithAutoClassificationAndChildRooms() {
        VillaRequest createReq = VillaRequest.builder()
                .villaNumber("TEST-VILLA-777")
                .floor(1)
                .structureType("1 Tầng")
                .basePrice(BigDecimal.valueOf(15000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .bedroomCount(2)
                .bedSelections(List.of(
                        VillaBedSelectionDto.builder().roomTypeId(1L).quantity(2).build()
                ))
                .build();

        VillaResponse created = villaService.createVilla(createReq);
        assertNotNull(created.getVillaTypeName());
        assertEquals(2, created.getRooms().size());

        // Update to 4 bedrooms
        VillaRequest updateReq = VillaRequest.builder()
                .villaNumber("TEST-VILLA-777")
                .floor(2)
                .structureType("2 Tầng")
                .basePrice(BigDecimal.valueOf(32000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.OCCUPIED)
                .bedroomCount(4)
                .amenities(List.of("Quản gia 24/7", "Xe điện buggy"))
                .bedSelections(List.of(
                        VillaBedSelectionDto.builder().roomTypeId(3L).quantity(4).build()
                ))
                .build();

        VillaResponse updated = villaService.updateVilla(created.getId(), updateReq);
        assertEquals("TEST-VILLA-777", updated.getVillaNumber());
        assertEquals("2 Tầng", updated.getStructureType());
        assertNotNull(updated.getVillaTypeName());
        assertEquals(4, updated.getBedroomCount());
        assertEquals(4, updated.getRooms().size());
        assertEquals(2, updated.getAmenities().size());
        assertEquals(VillaStatus.OCCUPIED, updated.getStatus());
    }

    @Test
    void testFallbackWhenChildRoomsEmpty() {
        // Create villa without rooms (legacy or direct)
        VillaType vt = villaTypeRepository.findByName("Villa 1 Phòng Ngủ")
                .orElseGet(() -> villaTypeRepository.save(VillaType.builder()
                        .name("Villa 1 Phòng Ngủ")
                        .basePrice(BigDecimal.valueOf(10000000))
                        .capacity(3)
                        .adults(2)
                        .children(1)
                        .build()));

        VillaRequest request = VillaRequest.builder()
                .villaNumber("TEST-VILLA-EMPTY-ROOMS")
                .villaTypeId(vt.getId())
                .bedroomCount(1)
                .zone("Ngọc Trai")
                .build();

        VillaResponse response = villaService.createVilla(request);
        assertNotNull(response);
        assertEquals(0, response.getRooms().size());
        // Verify fallback to VillaType capacity, adults, children
        assertEquals(2, response.getTotalAdults());
        assertEquals(1, response.getTotalChildren());
        assertEquals(3, response.getTotalCapacity());
    }
}
