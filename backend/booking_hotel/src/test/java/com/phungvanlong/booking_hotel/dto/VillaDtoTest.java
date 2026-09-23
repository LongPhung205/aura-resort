package com.phungvanlong.booking_hotel.dto;

import com.phungvanlong.booking_hotel.dto.request.ChildRoomRequestDto;
import com.phungvanlong.booking_hotel.dto.request.VillaBedSelectionDto;
import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.RoomType;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import com.phungvanlong.booking_hotel.entity.VillaType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VillaDtoTest {

    @Test
    void testVillaRequestAndResponseMapping() {
        VillaBedSelectionDto bed1 = VillaBedSelectionDto.builder()
                .roomTypeId(1L)
                .quantity(1)
                .build();

        VillaRequest request = VillaRequest.builder()
                .villaNumber("Villa #101")
                .floor(2)
                .structureType("2 Tầng")
                .basePrice(BigDecimal.valueOf(25000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .amenities(List.of("Khử trùng Ozon định kỳ", "Quản gia Lead Butler 24/7"))
                .bedroomCount(3)
                .bedSelections(List.of(bed1))
                .build();

        assertEquals("Villa #101", request.getVillaNumber());
        assertEquals("2 Tầng", request.getStructureType());
        assertEquals(2, request.getAmenities().size());

        RoomType kingRoomType = RoomType.builder()
                .id(1L)
                .name("King Size Bed")
                .bedType("Rộng 1.8m × Dài 2.0m")
                .adults(2)
                .children(1)
                .capacity(3)
                .build();

        Room room1 = Room.builder()
                .id(10L)
                .roomNumber("Villa #101-P1")
                .name("Phòng Ngủ Master")
                .floor(1)
                .roomType(kingRoomType)
                .build();

        VillaType villaType = VillaType.builder()
                .id(5L)
                .name("Villa 3 Phòng Ngủ")
                .build();

        Villa villa = Villa.builder()
                .id(100L)
                .villaNumber("Villa #101")
                .floor(2)
                .structureType("2 Tầng")
                .basePrice(BigDecimal.valueOf(25000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .ozoneStatus("STERILIZED")
                .amenities("Khử trùng Ozon định kỳ, Quản gia Lead Butler 24/7")
                .bedroomCount(3)
                .villaType(villaType)
                .rooms(List.of(room1))
                .build();

        VillaResponse response = VillaResponse.fromEntity(villa);
        assertNotNull(response);
        assertEquals("Villa #101", response.getVillaNumber());
        assertEquals("2 Tầng", response.getStructureType());
        assertEquals(BigDecimal.valueOf(25000000), response.getBasePrice());
        assertEquals(3, response.getBedroomCount());
        assertEquals(1, response.getRooms().size());
        assertEquals("King Size Bed", response.getRooms().get(0).getRoomTypeName());
        assertEquals(2, response.getTotalAdults());
        assertEquals(1, response.getTotalChildren());
    }
}
