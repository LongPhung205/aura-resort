package com.phungvanlong.booking_hotel.config;

import com.phungvanlong.booking_hotel.entity.*;
import com.phungvanlong.booking_hotel.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VillaTypeRepository villaTypeRepository;
    private final VillaRepository villaRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final ExtraServiceRepository extraServiceRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final ServiceDispatchRepository serviceDispatchRepository;
    private final HousekeepingTaskRepository housekeepingTaskRepository;
    private final ShiftRepository shiftRepository;
    private final StaffScheduleRepository staffScheduleRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final ShiftSwapRequestRepository shiftSwapRequestRepository;
    private final YieldRuleRepository yieldRuleRepository;
    private final PromotionRepository promotionRepository;
    private final ReviewRepository reviewRepository;
    private final ServiceRecoveryTicketRepository serviceRecoveryTicketRepository;
    private final PaymentRepository paymentRepository;
    private final DayEndClosingRepository dayEndClosingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting luxury homestay & villa resort database verification and seeding...");

        try {
            jdbcTemplate.execute("ALTER TABLE rooms MODIFY COLUMN room_type_id BIGINT NULL");
        } catch (Exception e) {
            log.warn("Could not alter rooms.room_type_id to nullable: {}", e.getMessage());
        }

        initUsers();
        // initVillaTypesAndVillas();
        initExtraServices();
        initShiftsAndSchedules();
        initYieldRules();
        initPromotions();
        // initBookingsAndDispatches();
        initPaymentsAndClosings();
        // initReviewsAndRecovery();

        log.info("Homestay & Villa database initialization completed successfully with 100% realistic data!");
    }

    private void initUsers() {
        if (userRepository.findByEmail("admin@auraholdings.vn").isEmpty()) {
            User admin = User.builder()
                    .email("admin@auraholdings.vn")
                    .fullName("Phạm Văn Minh (Tổng Quản Lý Homestay)")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("+84 908 112 334")
                    .role(Role.ROLE_ADMIN)
                    .isActive(true)
                    .build();

            User receptionist = User.builder()
                    .email("nam.reception@auraholdings.vn")
                    .fullName("Nguyễn Hoàng Nam (Lễ tân)")
                    .password(passwordEncoder.encode("staff123"))
                    .phone("+84 912 345 678")
                    .role(Role.ROLE_RECEPTIONIST)
                    .isActive(true)
                    .build();

            User butler = User.builder()
                    .email("hoang.butler@auraholdings.vn")
                    .fullName("Trần Văn Hoàng (Quản gia Homestay)")
                    .password(passwordEncoder.encode("staff123"))
                    .phone("+84 909 888 123")
                    .role(Role.ROLE_BUTLER)
                    .isActive(true)
                    .build();

            User housekeeper = User.builder()
                    .email("hoa.housekeeping@auraholdings.vn")
                    .fullName("Nguyễn Thị Hoa (Buồng phòng)")
                    .password(passwordEncoder.encode("staff123"))
                    .phone("+84 933 456 789")
                    .role(Role.ROLE_HOUSEKEEPING)
                    .isActive(true)
                    .build();

            User customer = User.builder()
                    .email("giahuy.tran@auraholdings.vn")
                    .fullName("Trần Gia Huy")
                    .password(passwordEncoder.encode("customer123"))
                    .phone("+84 918 223 999")
                    .role(Role.ROLE_CUSTOMER)
                    .isActive(true)
                    .build();

            userRepository.saveAll(Arrays.asList(admin, receptionist, butler, housekeeper, customer));
            log.info("Initialized 5 core accounts.");
        }
    }

    private void initVillaTypesAndVillas() {
        if (villaTypeRepository.count() == 0) {
            // 1. Khởi tạo 4 Hạng Villa Homestay
            VillaType vt1 = VillaType.builder()
                    .name("Grand Oceanfront Pool Villa")
                    .description("Bể bơi vô cực tràn biển riêng, phòng khách mở toàn cảnh đại dương, bếp nấu nướng BBQ sân vườn và quản gia riêng 24/7.")
                    .basePrice(BigDecimal.valueOf(38500000))
                    .dynamicPrice(BigDecimal.valueOf(44200000))
                    .isDynamicPricingEnabled(true)
                    .capacity(8)
                    .bedType("3 Giường King & Queen")
                    .imageUrl("/assets/images/rooms/grand-oceanfront.jpg")
                    .build();

            VillaType vt2 = VillaType.builder()
                    .name("Sunset Lagoon Serenity Villa")
                    .description("Bên đầm nước tự nhiên ngắm trọn hoàng hôn buông xuống vịnh biển, ban công kính tràn viền, bể sục jacuzzi và bếp ăn gia đình.")
                    .basePrice(BigDecimal.valueOf(24500000))
                    .dynamicPrice(BigDecimal.valueOf(28000000))
                    .isDynamicPricingEnabled(true)
                    .capacity(4)
                    .bedType("2 Giường King & Queen")
                    .imageUrl("/assets/images/rooms/sunset-lagoon.jpg")
                    .build();

            VillaType vt3 = VillaType.builder()
                    .name("Cliffside Sunset Villa")
                    .description("Ngự trên vách đá nhìn xuống vịnh san hô ngọc bích, bồn tắm jacuzzi khoáng nóng lộ thiên, hiên tắm nắng và sân BBQ.")
                    .basePrice(BigDecimal.valueOf(29000000))
                    .dynamicPrice(BigDecimal.valueOf(33500000))
                    .isDynamicPricingEnabled(true)
                    .capacity(6)
                    .bedType("3 Giường King & Twin")
                    .imageUrl("/assets/images/rooms/cliffside-sunset.jpg")
                    .build();

            VillaType vt4 = VillaType.builder()
                    .name("Royal Presidential Suite")
                    .description("2 tầng penthouse đỉnh cao danh vọng, bãi đỗ trực thăng riêng, rạp chiếu phim Dolby Atmos và hồ bơi vô cực đôi.")
                    .basePrice(BigDecimal.valueOf(165000000))
                    .dynamicPrice(BigDecimal.valueOf(181500000))
                    .isDynamicPricingEnabled(true)
                    .capacity(10)
                    .bedType("4 Giường King Siêu Lớn")
                    .imageUrl("/assets/images/rooms/presidential-suite.jpg")
                    .build();

            List<VillaType> savedVillaTypes = villaTypeRepository.saveAll(Arrays.asList(vt1, vt2, vt3, vt4));
            log.info("Initialized 4 luxury Villa Types.");

            // Also seed RoomType for backward compatibility if empty
            if (roomTypeRepository.count() == 0) {
                RoomType rt1 = RoomType.builder().name(vt1.getName()).description(vt1.getDescription()).basePrice(vt1.getBasePrice()).dynamicPrice(vt1.getDynamicPrice()).capacity(vt1.getCapacity()).bedType(vt1.getBedType()).imageUrl(vt1.getImageUrl()).build();
                RoomType rt2 = RoomType.builder().name(vt2.getName()).description(vt2.getDescription()).basePrice(vt2.getBasePrice()).dynamicPrice(vt2.getDynamicPrice()).capacity(vt2.getCapacity()).bedType(vt2.getBedType()).imageUrl(vt2.getImageUrl()).build();
                RoomType rt3 = RoomType.builder().name(vt3.getName()).description(vt3.getDescription()).basePrice(vt3.getBasePrice()).dynamicPrice(vt3.getDynamicPrice()).capacity(vt3.getCapacity()).bedType(vt3.getBedType()).imageUrl(vt3.getImageUrl()).build();
                RoomType rt4 = RoomType.builder().name(vt4.getName()).description(vt4.getDescription()).basePrice(vt4.getBasePrice()).dynamicPrice(vt4.getDynamicPrice()).capacity(vt4.getCapacity()).bedType(vt4.getBedType()).imageUrl(vt4.getImageUrl()).build();
                roomTypeRepository.saveAll(Arrays.asList(rt1, rt2, rt3, rt4));
            }

            // 2. Khởi tạo 12 Căn Villa Vật Lý
            Villa v101 = Villa.builder().villaNumber("Villa #801").floor(1).zone("Khu A - Biển Đông").status(VillaStatus.OCCUPIED).ozoneStatus("STERILIZED").currentGuestName("Trần Gia Huy").villaType(savedVillaTypes.get(0)).build();
            Villa v102 = Villa.builder().villaNumber("Villa #802").floor(1).zone("Khu A - Biển Đông").status(VillaStatus.CLEANING).ozoneStatus("RUNNING").villaType(savedVillaTypes.get(0)).build();
            Villa v103 = Villa.builder().villaNumber("Villa #803").floor(2).zone("Khu A - Biển Đông").status(VillaStatus.AVAILABLE).ozoneStatus("STERILIZED").villaType(savedVillaTypes.get(0)).build();
            Villa v104 = Villa.builder().villaNumber("Villa #804").floor(2).zone("Khu A - Biển Đông").status(VillaStatus.OCCUPIED).ozoneStatus("STERILIZED").currentGuestName("Lê Hoàng Yến").villaType(savedVillaTypes.get(0)).build();

            Villa v201 = Villa.builder().villaNumber("Villa #601").floor(1).zone("Khu B - Đầm Hoàng Hôn").status(VillaStatus.OCCUPIED).ozoneStatus("STERILIZED").currentGuestName("Trần Đình Trọng").villaType(savedVillaTypes.get(1)).build();
            Villa v202 = Villa.builder().villaNumber("Villa #602").floor(1).zone("Khu B - Đầm Hoàng Hôn").status(VillaStatus.AVAILABLE).ozoneStatus("STERILIZED").villaType(savedVillaTypes.get(1)).build();
            Villa v203 = Villa.builder().villaNumber("Villa #603").floor(2).zone("Khu B - Đầm Hoàng Hôn").status(VillaStatus.CLEANING).ozoneStatus("RUNNING").villaType(savedVillaTypes.get(1)).build();
            Villa v204 = Villa.builder().villaNumber("Villa #604").floor(2).zone("Khu B - Đầm Hoàng Hôn").status(VillaStatus.AVAILABLE).ozoneStatus("STERILIZED").villaType(savedVillaTypes.get(1)).build();

            Villa v301 = Villa.builder().villaNumber("Villa #701").floor(1).zone("Khu C - Vách Đá").status(VillaStatus.OCCUPIED).ozoneStatus("STERILIZED").currentGuestName("Phạm Minh Long").villaType(savedVillaTypes.get(2)).build();
            Villa v302 = Villa.builder().villaNumber("Villa #702").floor(1).zone("Khu C - Vách Đá").status(VillaStatus.AVAILABLE).ozoneStatus("STERILIZED").villaType(savedVillaTypes.get(2)).build();
            Villa v303 = Villa.builder().villaNumber("Villa #703").floor(2).zone("Khu C - Vách Đá").status(VillaStatus.AVAILABLE).ozoneStatus("STERILIZED").villaType(savedVillaTypes.get(2)).build();
            Villa v304 = Villa.builder().villaNumber("Villa #901").floor(3).zone("Khu C - Vách Đá").status(VillaStatus.OCCUPIED).ozoneStatus("STERILIZED").currentGuestName("Vương Khắc Triệu (Black Card)").villaType(savedVillaTypes.get(3)).build();

            List<Villa> savedVillas = villaRepository.saveAll(Arrays.asList(v101, v102, v103, v104, v201, v202, v203, v204, v301, v302, v303, v304));
            log.info("Initialized 12 luxury physical Villas.");

            // 3. Khởi tạo các Phòng ngủ con (Room) bên trong từng căn Villa
            RoomType defaultRt = roomTypeRepository.findAll().stream().findFirst().orElse(null);
            List<Room> childRooms = new ArrayList<>();
            for (Villa v : savedVillas) {
                childRooms.add(Room.builder()
                        .roomNumber(v.getVillaNumber() + "-MB")
                        .name("Master Bedroom")
                        .description("Phòng ngủ chính view hướng biển/thiên nhiên, giường King-size 2mx2m2, ban công riêng và bồn tắm ngâm thảo dược.")
                        .floor(v.getFloor())
                        .status(RoomStatus.AVAILABLE)
                        .zone(v.getZone())
                        .villa(v)
                        .roomType(defaultRt)
                        .build());

                childRooms.add(Room.builder()
                        .roomNumber(v.getVillaNumber() + "-B2")
                        .name("Bedroom 2 (Deluxe Double)")
                        .description("Phòng ngủ phụ sang trọng, giường Queen-size 1m8x2m, cửa sổ kính tràn panorama và bàn làm việc yên tĩnh.")
                        .floor(v.getFloor())
                        .status(RoomStatus.AVAILABLE)
                        .zone(v.getZone())
                        .villa(v)
                        .roomType(defaultRt)
                        .build());

                if (v.getVillaType().getCapacity() >= 6) {
                    childRooms.add(Room.builder()
                            .roomNumber(v.getVillaNumber() + "-B3")
                            .name("Bedroom 3 (Twin Suite)")
                            .description("Phòng 2 giường đơn 1m2x2m linh hoạt cho con nhỏ hoặc nhóm bạn, phòng tắm khép kín.")
                            .floor(v.getFloor())
                            .status(RoomStatus.AVAILABLE)
                            .zone(v.getZone())
                            .villa(v)
                            .roomType(defaultRt)
                            .build());
                }

                if (v.getVillaType().getCapacity() >= 8) {
                    childRooms.add(Room.builder()
                            .roomNumber(v.getVillaNumber() + "-B4")
                            .name("Bedroom 4 (Presidential Bedroom)")
                            .description("Phòng ngủ áp mái cao cấp nhất với tầm nhìn 360 độ, phòng xông hơi khô sauna và quầy bar mini riêng.")
                            .floor(v.getFloor() + 1)
                            .status(RoomStatus.AVAILABLE)
                            .zone(v.getZone())
                            .villa(v)
                            .roomType(defaultRt)
                            .build());
                }
            }

            roomRepository.saveAll(childRooms);
            log.info("Initialized {} child bedrooms inside 12 luxury Villas.", childRooms.size());
        }
    }

    private void initExtraServices() {
        if (extraServiceRepository.count() == 0) {
            ExtraService s1 = ExtraService.builder()
                    .name("Đón Sân Bay Maybach S680 VIP")
                    .description("Nội thất First-Class thương gia, champagne Dom Pérignon ướp lạnh sẵn, tài xế riêng phục vụ 24/7.")
                    .price(BigDecimal.valueOf(3500000))
                    .build();

            ExtraService s2 = ExtraService.builder()
                    .name("Hải Trình Du Thuyền Siêu Sang Aura Pearl")
                    .description("Du ngoạn vịnh biển ngắm hoàng hôn 3 giờ, phục vụ tiệc canapé tôm hùm bông và rượu vang thượng hạng.")
                    .price(BigDecimal.valueOf(18000000))
                    .build();

            ExtraService s3 = ExtraService.builder()
                    .name("Set BBQ Sân Vườn Villa & Bếp Nướng Than Hoa")
                    .description("Bò Wagyu A5 nướng than hoa tại sân vườn villa, hải sản tươi sống và đầu bếp riêng phục vụ tại chỗ.")
                    .price(BigDecimal.valueOf(4500000))
                    .build();

            ExtraService s4 = ExtraService.builder()
                    .name("Liệu Trình Spa Hoàng Gia Lotus Luxury")
                    .description("90 phút massage đá nóng Himalaya và tinh dầu trầm hương hữu cơ trẻ hóa tế bào.")
                    .price(BigDecimal.valueOf(2200000))
                    .build();

            extraServiceRepository.saveAll(Arrays.asList(s1, s2, s3, s4));
            log.info("Initialized 4 high-end homestay/villa extra services.");
        }
    }

    private void initShiftsAndSchedules() {
        if (shiftRepository.count() == 0) {
            Shift shiftMorning = Shift.builder().name("Ca Sáng (Bình Minh)").startTime(LocalTime.of(6, 0)).endTime(LocalTime.of(14, 30)).build();
            Shift shiftAfternoon = Shift.builder().name("Ca Chiều (Hoàng Hôn)").startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(22, 30)).build();
            Shift shiftNight = Shift.builder().name("Ca Đêm (Bảo An)").startTime(LocalTime.of(22, 0)).endTime(LocalTime.of(6, 30)).build();
            List<Shift> savedShifts = shiftRepository.saveAll(Arrays.asList(shiftMorning, shiftAfternoon, shiftNight));

            User hoang = userRepository.findByEmail("hoang.butler@auraholdings.vn").orElse(null);
            User hoa = userRepository.findByEmail("hoa.housekeeping@auraholdings.vn").orElse(null);
            LocalDate today = LocalDate.now();

            if (hoang != null) {
                staffScheduleRepository.save(StaffSchedule.builder().staff(hoang).shift(savedShifts.get(0)).workDate(today).status("PRESENT").build());
            }
            if (hoa != null) {
                staffScheduleRepository.save(StaffSchedule.builder().staff(hoa).shift(savedShifts.get(0)).workDate(today).status("PRESENT").build());
            }
            log.info("Initialized shifts and initial staff schedules.");
        }
    }

    private void initYieldRules() {
        if (yieldRuleRepository.count() == 0) {
            YieldRule r1 = YieldRule.builder()
                    .ruleName("Ngưỡng lấp đầy > 85% (Tự động tăng giá 20%)")
                    .conditionType("OCCUPANCY_THRESHOLD")
                    .thresholdValue(85.0)
                    .priceMultiplier(1.20)
                    .targetVillaTypes("ALL")
                    .targetRoomTypes("ALL")
                    .isActive(true)
                    .description("Tự động kích hoạt khi công suất toàn khu vượt 85%")
                    .build();

            YieldRule r2 = YieldRule.builder()
                    .ruleName("Phụ thu Cuối tuần & Lễ hội (+25%)")
                    .conditionType("HOLIDAY_PEAK")
                    .thresholdValue(0.0)
                    .priceMultiplier(1.25)
                    .targetVillaTypes("ALL")
                    .targetRoomTypes("ALL")
                    .isActive(true)
                    .description("Áp dụng cho các đêm Thứ 6, Thứ 7 và dịp lễ lớn")
                    .build();

            yieldRuleRepository.saveAll(Arrays.asList(r1, r2));
            log.info("Initialized dynamic pricing yield rules.");
        }
    }

    private void initPromotions() {
        if (promotionRepository.count() == 0) {
            Promotion p1 = Promotion.builder()
                    .code("SUMMER-ELITE-2026")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(20))
                    .startDate(LocalDate.now().minusDays(5))
                    .endDate(LocalDate.now().plusDays(60))
                    .quantity(100)
                    .build();

            Promotion p2 = Promotion.builder()
                    .code("ROYAL-HONEYMOON")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(15))
                    .startDate(LocalDate.now().minusDays(10))
                    .endDate(LocalDate.now().plusDays(90))
                    .quantity(50)
                    .build();

            promotionRepository.saveAll(Arrays.asList(p1, p2));
            log.info("Initialized promotional campaigns.");
        }
    }

    private void initBookingsAndDispatches() {
        if (bookingRepository.count() == 0) {
            User guest = userRepository.findByEmail("giahuy.tran@auraholdings.vn").orElse(null);
            Villa v101 = villaRepository.findByVillaNumber("Villa #801").orElse(null);
            Villa v102 = villaRepository.findByVillaNumber("Villa #802").orElse(null);

            if (guest != null && v101 != null) {
                Booking b1 = Booking.builder()
                        .bookingCode("BK-202609-001")
                        .user(guest)
                        .guestName("Trần Gia Huy (Diamond VIP)")
                        .guestPhone("+84 918 223 999")
                        .guestEmail("giahuy.tran@auraholdings.vn")
                        .idCardNumber("079099001234")
                        .checkInDate(LocalDate.now().minusDays(1))
                        .checkOutDate(LocalDate.now().plusDays(2))
                        .checkInTime(LocalDateTime.now().minusDays(1).withHour(14).withMinute(15))
                        .depositAmount(BigDecimal.valueOf(20000000))
                        .folioBalance(BigDecimal.valueOf(118500000))
                        .totalAmount(BigDecimal.valueOf(118500000))
                        .status(BookingStatus.CHECKED_IN)
                        .note("Khách VIP Diamond yêu cầu champagne Dom Pérignon và tiệc BBQ sân vườn.")
                        .build();

                Booking savedBooking = bookingRepository.save(b1);

                BookingDetail detail = BookingDetail.builder()
                        .booking(savedBooking)
                        .villa(v101)
                        .pricePerNight(BigDecimal.valueOf(38500000))
                        .build();
                bookingDetailRepository.save(detail);

                // Service dispatch for Maybach
                User hoang = userRepository.findByEmail("hoang.butler@auraholdings.vn").orElse(null);
                serviceDispatchRepository.save(ServiceDispatch.builder()
                        .booking(savedBooking)
                        .serviceType("Maybach S680")
                        .assetCode("51H-999.88")
                        .guestName("Trần Gia Huy")
                        .roomNumber("Villa #801")
                        .assignedStaff(hoang)
                        .pickupLocation("Sân bay Cam Ranh (Ga Quốc tế)")
                        .destination("Aura Horizon Homestay - Villa #801")
                        .scheduledTime(LocalDateTime.now().plusHours(3))
                        .flightNumber("VN-1234")
                        .status("DISPATCHED")
                        .cost(BigDecimal.valueOf(3500000))
                        .notes("Đón khách VIP theo tiêu chuẩn First-Class")
                        .build());
            }

            if (v102 != null) {
                User hoa = userRepository.findByEmail("hoa.housekeeping@auraholdings.vn").orElse(null);
                housekeepingTaskRepository.save(HousekeepingTask.builder()
                        .villa(v102)
                        .housekeeper(hoa)
                        .taskType("CHECKOUT_DEEP")
                        .status("IN_PROGRESS")
                        .startedAt(LocalDateTime.now().minusMinutes(15))
                        .ozoneStartedAt(LocalDateTime.now().minusMinutes(15))
                        .ozoneEndedAt(LocalDateTime.now().plusMinutes(15))
                        .cleaningNote("Đang thay ga trải giường và vệ sinh hồ bơi riêng")
                        .supervisorNote("Đang dọn dẹp vệ sinh theo tiêu chuẩn 16 bước Homestay Luxury")
                        .build());
            }
            log.info("Initialized bookings, dispatches, and housekeeping tasks.");
        }
    }

    private void initPaymentsAndClosings() {
        if (paymentRepository.count() == 0) {
            Booking b = bookingRepository.findAll().stream().findFirst().orElse(null);
            if (b != null) {
                Payment p1 = Payment.builder()
                        .booking(b)
                        .transactionId("TXN-MOMO-98214")
                        .referenceNo("REF-202609-001")
                        .guestName(b.getGuestName())
                        .amount(BigDecimal.valueOf(38500000))
                        .paymentMethod("MOMO")
                        .ledgerType("ROOM_CHARGE")
                        .status(PaymentStatus.SUCCESS)
                        .paymentTime(LocalDateTime.now().minusDays(1))
                        .build();

                Payment p2 = Payment.builder()
                        .booking(b)
                        .transactionId("TXN-POS-44810")
                        .referenceNo("POS-SẢNH-CHÍNH")
                        .guestName(b.getGuestName())
                        .amount(BigDecimal.valueOf(3500000))
                        .paymentMethod("POS_TERMINAL")
                        .ledgerType("EXTRA_SERVICE")
                        .status(PaymentStatus.SUCCESS)
                        .paymentTime(LocalDateTime.now().minusHours(4))
                        .build();

                paymentRepository.saveAll(Arrays.asList(p1, p2));
            }

            if (dayEndClosingRepository.count() == 0) {
                User admin = userRepository.findByEmail("admin@auraholdings.vn").orElse(null);
                dayEndClosingRepository.save(DayEndClosing.builder()
                        .closingDate(LocalDate.now().minusDays(1))
                        .totalRevenue(BigDecimal.valueOf(185000000))
                        .roomRevenue(BigDecimal.valueOf(142000000))
                        .serviceRevenue(BigDecimal.valueOf(43000000))
                        .totalOpex(BigDecimal.valueOf(51800000))
                        .netCash(BigDecimal.valueOf(133200000))
                        .occupancyRate(91.6)
                        .adr(BigDecimal.valueOf(15400000))
                        .revPar(BigDecimal.valueOf(14100000))
                        .totalBookings(12)
                        .occupiedRooms(11)
                        .closedBy(admin)
                        .closedAt(LocalDateTime.now().minusDays(1).withHour(23).withMinute(59))
                        .status("LOCKED")
                        .notes("Đã hoàn tất đối soát 100% khớp các cổng MoMo, VNPay, POS tại quầy và két sắt lễ tân")
                        .build());
            }
            log.info("Initialized payments and day-end closings.");
        }
    }

    private void initReviewsAndRecovery() {
        if (reviewRepository.count() == 0) {
            Booking b = bookingRepository.findAll().stream().findFirst().orElse(null);
            VillaType vt = villaTypeRepository.findAll().stream().findFirst().orElse(null);

            if (b != null && vt != null) {
                Review rev = Review.builder()
                        .booking(b)
                        .villaType(vt)
                        .rating(5)
                        .guestName(b.getGuestName())
                        .sentiment("POSITIVE")
                        .comment("Kỳ nghỉ tuyệt hảo tại Villa #801. Quản gia Hoàng cực kỳ chu đáo, chuẩn bị champagne và nến hoàng hôn lãng mạn bên hồ bơi.")
                        .managementReply("Kính gửi Quý khách, Aura Horizon Homestay & Resort vô cùng vinh hạnh khi mang lại kỳ nghỉ tuyệt vời cho Quý khách. Hân hạnh được chào đón Quý khách trong những hành trình tiếp theo!")
                        .repliedAt(LocalDateTime.now().minusHours(1))
                        .build();

                Review savedRev = reviewRepository.save(rev);

                User admin = userRepository.findByEmail("admin@auraholdings.vn").orElse(null);
                serviceRecoveryTicketRepository.save(ServiceRecoveryTicket.builder()
                        .review(savedRev)
                        .guestName("Hoàng Bích Ngọc")
                        .roomNumber("Villa #603")
                        .incidentCategory("FOOD_AND_BEVERAGE")
                        .issueSummary("Món bò Wagyu A5 tại tiệc BBQ bãi biển chín quá mức mong muốn (medium-rare thành medium-well)")
                        .resolutionAction("Bếp trưởng đích thân đổi phần thăn Wagyu mới + Tặng 1 chai vang đỏ Grand Cru")
                        .assignedManager(admin)
                        .slaMinutes(3)
                        .actualResolutionMinutes(2)
                        .status("RESOLVED")
                        .resolvedAt(LocalDateTime.now().minusHours(3))
                        .build());
            }
            log.info("Initialized reviews and service recovery tickets.");
        }
    }
}
