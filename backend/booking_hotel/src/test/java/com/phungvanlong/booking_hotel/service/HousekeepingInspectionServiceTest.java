package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.SubmitRoomInspectionRequest;
import com.phungvanlong.booking_hotel.dto.response.RoomConsumptionResponse;
import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HousekeepingInspectionServiceTest {

    @Autowired
    private HousekeepingInspectionService inspectionService;

    @Autowired
    private HousekeepingTaskRepository housekeepingTaskRepository;

    @Autowired
    private VillaRepository villaRepository;

    @Autowired
    private VillaTypeRepository villaTypeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    private HousekeepingTask testTask;
    private Booking testBooking;
    private Villa testVilla;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        VillaType vt = villaTypeRepository.save(VillaType.builder()
                .name("Villa Type Test")
                .basePrice(new BigDecimal("3000000"))
                .adults(2)
                .children(1)
                .capacity(3)
                .build());

        testVilla = villaRepository.save(Villa.builder()
                .villaNumber("V-TEST-99")
                .villaType(vt)
                .status(VillaStatus.CLEANING)
                .build());

        testRoom = roomRepository.save(Room.builder()
                .villa(testVilla)
                .roomNumber("R-TEST-99")
                .name("Master Bedroom Test")
                .build());

        User guest = userRepository.save(User.builder()
                .email("guest.test@example.com")
                .fullName("Khách Test")
                .phone("0988776655")
                .password("pass123")
                .role(Role.ROLE_CUSTOMER)
                .build());

        testBooking = bookingRepository.save(Booking.builder()
                .bookingCode("BK-TEST-HK-01")
                .checkInDate(LocalDate.now().minusDays(1))
                .checkOutDate(LocalDate.now())
                .totalAmount(new BigDecimal("3000000"))
                .depositAmount(new BigDecimal("1000000"))
                .folioBalance(BigDecimal.ZERO)
                .status(BookingStatus.CONFIRMED)
                .user(guest)
                .build());

        testTask = housekeepingTaskRepository.save(HousekeepingTask.builder()
                .villa(testVilla)
                .room(testRoom)
                .booking(testBooking)
                .taskType("CHECKOUT_DEEP")
                .status("IN_PROGRESS")
                .priority("NORMAL")
                .build());
    }

    @Test
    @DisplayName("Kiểm tra nộp kiểm kê minibar và tài sản tính đúng số lượng và số tiền")
    void testSubmitInspectionCalculation() {
        SubmitRoomInspectionRequest.MinibarItemInspectionDto beer = SubmitRoomInspectionRequest.MinibarItemInspectionDto.builder()
                .itemName("Bia Heineken")
                .standardQuantity(4)
                .currentQuantity(2) // Đã dùng 2
                .unitPrice(new BigDecimal("35000"))
                .build();

        SubmitRoomInspectionRequest.AssetIncidentReportDto towel = SubmitRoomInspectionRequest.AssetIncidentReportDto.builder()
                .itemName("Khăn tắm lớn 70x140")
                .incidentType(RoomConsumptionItemType.ASSET_DAMAGED)
                .quantity(1)
                .compensationPrice(new BigDecimal("200000"))
                .note("Khách làm rách vải")
                .build();

        SubmitRoomInspectionRequest request = SubmitRoomInspectionRequest.builder()
                .minibarItems(List.of(beer))
                .damagedOrLostAssets(List.of(towel))
                .amenitiesComplimentaryConfirmed(true)
                .build();

        List<RoomConsumptionResponse> responses = inspectionService.submitInspection(testTask.getId(), request, "staff@auroresort.com");

        assertEquals(2, responses.size());

        RoomConsumptionResponse beerResp = responses.stream()
                .filter(r -> r.getItemType() == RoomConsumptionItemType.MINIBAR_CONSUMED)
                .findFirst().orElse(null);
        assertNotNull(beerResp);
        assertEquals(2, beerResp.getQuantity());
        assertEquals(new BigDecimal("70000"), beerResp.getTotalPrice());
        assertEquals(RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL, beerResp.getStatus());

        RoomConsumptionResponse towelResp = responses.stream()
                .filter(r -> r.getItemType() == RoomConsumptionItemType.ASSET_DAMAGED)
                .findFirst().orElse(null);
        assertNotNull(towelResp);
        assertEquals(1, towelResp.getQuantity());
        assertEquals(new BigDecimal("200000"), towelResp.getTotalPrice());
        assertEquals(RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL, towelResp.getStatus());
    }

    @Test
    @DisplayName("Lễ tân duyệt khoản tiêu thụ minibar thì Folio của Booking được cập nhật")
    void testApproveConsumptionUpdatesBookingFolio() {
        SubmitRoomInspectionRequest.MinibarItemInspectionDto snack = SubmitRoomInspectionRequest.MinibarItemInspectionDto.builder()
                .itemName("Hạt điều")
                .standardQuantity(2)
                .currentQuantity(1) // Đã dùng 1
                .unitPrice(new BigDecimal("50000"))
                .build();

        SubmitRoomInspectionRequest request = SubmitRoomInspectionRequest.builder()
                .minibarItems(List.of(snack))
                .build();

        List<RoomConsumptionResponse> responses = inspectionService.submitInspection(testTask.getId(), request, "staff@auroresort.com");
        assertEquals(1, responses.size());
        Long consumptionId = responses.get(0).getId();

        RoomConsumptionResponse approved = inspectionService.approveConsumption(consumptionId, "reception@auroresort.com");
        assertEquals(RoomConsumptionStatus.APPROVED_CHARGED, approved.getStatus());

        Booking reloadedBooking = bookingRepository.findById(testBooking.getId()).orElseThrow();
        assertEquals(new BigDecimal("50000.00"), reloadedBooking.getFolioBalance().setScale(2));
    }
}
