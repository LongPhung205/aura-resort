package com.phungvanlong.booking_hotel.config;

import com.phungvanlong.booking_hotel.entity.ExtraService;
import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.repository.ExtraServiceRepository;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final ExtraServiceRepository extraServiceRepository;
    private final com.phungvanlong.booking_hotel.repository.InventoryItemRepository inventoryItemRepository;
    private final com.phungvanlong.booking_hotel.repository.InventoryTransactionRepository inventoryTransactionRepository;
    private final com.phungvanlong.booking_hotel.repository.VillaAssetRepository villaAssetRepository;
    private final com.phungvanlong.booking_hotel.repository.VillaSupplyStandardRepository villaSupplyStandardRepository;
    private final com.phungvanlong.booking_hotel.repository.VillaInventoryRepository villaInventoryRepository;
    private final com.phungvanlong.booking_hotel.repository.RefillTaskRepository refillTaskRepository;
    private final com.phungvanlong.booking_hotel.repository.VillaRepository villaRepository;
    private final com.phungvanlong.booking_hotel.repository.HomeBannerRepository homeBannerRepository;
    private final com.phungvanlong.booking_hotel.repository.ZoneRepository zoneRepository;
    private final com.phungvanlong.booking_hotel.repository.HousekeepingTaskRepository housekeepingTaskRepository;
    private final com.phungvanlong.booking_hotel.repository.RoomConsumptionRecordRepository roomConsumptionRecordRepository;
    private final com.phungvanlong.booking_hotel.repository.LostAndFoundItemRepository lostAndFoundItemRepository;
    private final com.phungvanlong.booking_hotel.repository.MaintenanceTicketRepository maintenanceTicketRepository;
    private final com.phungvanlong.booking_hotel.repository.RoomRepository roomRepository;
    private final com.phungvanlong.booking_hotel.repository.BookingRepository bookingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting luxury homestay & villa resort database verification...");

        try {
            jdbcTemplate.execute("ALTER TABLE rooms MODIFY COLUMN room_type_id BIGINT NULL");
        } catch (Exception e) {
            log.warn("Could not alter rooms.room_type_id to nullable: {}", e.getMessage());
        }

        initUsers();
        initExtraServices();
        initInventoryData();
        initRefillStandardsAndTasks();
        initBannersAndZones();
        initHousekeepingData();

        log.info("Database verification completed successfully (Clean state - Users preserved).");
    }

    private void initUsers() {
        if (userRepository.findByEmail("admin@auraholdings.vn").isEmpty()) {
            User admin = User.builder()
                    .email("admin@auraholdings.vn")
                    .fullName("Phạm Văn Minh (Tổng Quản Lý Resort)")
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
                    .fullName("Trần Văn Hoàng (Quản gia VIP)")
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

    private void initExtraServices() {
        if (extraServiceRepository.count() > 0) {
            log.info("Extra services already exist, skipping seed.");
            return;
        }

        ExtraService s1 = ExtraService.builder()
                .name("Ăn sáng").description("Bữa sáng buffet cao cấp tại villa hoặc nhà hàng")
                .price(new BigDecimal("80000")).type("DINING").unit("người").icon("restaurant").isActive(true).build();

        ExtraService s2 = ExtraService.builder()
                .name("BBQ").description("Tiệc BBQ ngoài trời bên bờ biển với hải sản tươi sống")
                .price(new BigDecimal("500000")).type("DINING").unit("gói").icon("outdoor_grill").isActive(true).build();

        ExtraService s3 = ExtraService.builder()
                .name("Thuê xe máy").description("Xe máy tay ga Honda Air Blade / Lead giao tận villa")
                .price(new BigDecimal("150000")).type("TRANSPORT").unit("ngày").icon("two_wheeler").isActive(true).build();

        ExtraService s4 = ExtraService.builder()
                .name("Dọn phòng").description("Dọn vệ sinh villa theo yêu cầu ngoài lịch trình")
                .price(new BigDecimal("100000")).type("CLEANING").unit("lần").icon("cleaning_services").isActive(true).build();

        ExtraService s5 = ExtraService.builder()
                .name("Trang trí sinh nhật").description("Gói trang trí sinh nhật với bóng bay, bánh kem và hoa tươi")
                .price(new BigDecimal("800000")).type("ENTERTAINMENT").unit("gói").icon("celebration").isActive(true).build();

        ExtraService s6 = ExtraService.builder()
                .name("Đưa đón sân bay").description("Xe riêng đón/tiễn sân bay Phú Quốc - Villa")
                .price(new BigDecimal("300000")).type("TRANSPORT").unit("chuyến").icon("airport_shuttle").isActive(true).build();

        ExtraService s7 = ExtraService.builder()
                .name("Thuê bếp").description("Thuê không gian bếp villa để tự nấu ăn")
                .price(new BigDecimal("200000")).type("DINING").unit("ngày").icon("cooking").isActive(true).build();

        extraServiceRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5, s6, s7));
        log.info("Initialized 7 default extra services.");
    }

    private void initInventoryData() {
        if (inventoryItemRepository.count() > 0) {
            log.info("Inventory items already exist, skipping seed.");
            return;
        }

        com.phungvanlong.booking_hotel.entity.InventoryItem item1 = com.phungvanlong.booking_hotel.entity.InventoryItem.builder()
                .code("AMN-TOOTH-01")
                .name("Bàn chải & Kem đánh răng sinh học")
                .category(com.phungvanlong.booking_hotel.entity.InventoryCategory.AMENITY)
                .unit("Bộ")
                .inStock(480)
                .minThreshold(100)
                .unitPrice(new BigDecimal("12000"))
                .supplier("EcoSupply Vietnam")
                .location("Kho Tổng A1")
                .description("Bộ đồ dùng cá nhân thân thiện môi trường tiêu chuẩn 5 sao")
                .build();

        com.phungvanlong.booking_hotel.entity.InventoryItem item2 = com.phungvanlong.booking_hotel.entity.InventoryItem.builder()
                .code("AMN-SHAMP-02")
                .name("Dầu gội & Sữa tắm hữu cơ Aura (50ml)")
                .category(com.phungvanlong.booking_hotel.entity.InventoryCategory.AMENITY)
                .unit("Chai")
                .inStock(320)
                .minThreshold(150)
                .unitPrice(new BigDecimal("35000"))
                .supplier("Natural Care Ltd")
                .location("Kho Tổng A1")
                .description("Sữa tắm hữu cơ tinh dầu tràm & oải hương")
                .build();

        com.phungvanlong.booking_hotel.entity.InventoryItem item3 = com.phungvanlong.booking_hotel.entity.InventoryItem.builder()
                .code("LIN-TOWL-03")
                .name("Khăn tắm cao cấp 100% Cotton Ai Cập (70x140cm)")
                .category(com.phungvanlong.booking_hotel.entity.InventoryCategory.LINEN)
                .unit("Chiếc")
                .inStock(85)
                .minThreshold(100)
                .unitPrice(new BigDecimal("185000"))
                .supplier("Dệt May Phong Phú")
                .location("Kho Vải Linen B2")
                .description("Khăn bông tắm cao cấp thấm hút tốt dập nổi logo Aura")
                .build();

        com.phungvanlong.booking_hotel.entity.InventoryItem item4 = com.phungvanlong.booking_hotel.entity.InventoryItem.builder()
                .code("MINI-WINE-04")
                .name("Vang Đỏ Chateau Dalat Reserve 750ml")
                .category(com.phungvanlong.booking_hotel.entity.InventoryCategory.MINIBAR)
                .unit("Chai")
                .inStock(24)
                .minThreshold(15)
                .unitPrice(new BigDecimal("420000"))
                .supplier("Dalat Wine Corp")
                .location("Kho Bar & F&B C1")
                .description("Vang đỏ cao cấp dành riêng cho tủ minibar villa VIP")
                .build();

        com.phungvanlong.booking_hotel.entity.InventoryItem item5 = com.phungvanlong.booking_hotel.entity.InventoryItem.builder()
                .code("CLN-OZONE-05")
                .name("Dung dịch khử khuẩn chuyên dụng máy Ozon")
                .category(com.phungvanlong.booking_hotel.entity.InventoryCategory.CLEANING)
                .unit("Can 5L")
                .inStock(12)
                .minThreshold(8)
                .unitPrice(new BigDecimal("650000"))
                .supplier("Hóa Chất Khử Khuẩn 3M")
                .location("Kho Hóa Chất Buồng Phòng")
                .description("Dung dịch diệt khuẩn chuyên dùng cho chu trình làm sạch Ozon")
                .build();

        inventoryItemRepository.saveAll(Arrays.asList(item1, item2, item3, item4, item5));
        log.info("Initialized 5 inventory items.");

        // Transactions
        com.phungvanlong.booking_hotel.entity.InventoryTransaction tx1 = com.phungvanlong.booking_hotel.entity.InventoryTransaction.builder()
                .item(item1)
                .type(com.phungvanlong.booking_hotel.entity.InventoryTransactionType.IMPORT)
                .quantity(300)
                .unitPrice(item1.getUnitPrice())
                .totalAmount(item1.getUnitPrice().multiply(BigDecimal.valueOf(300)))
                .reason("Nhập định kỳ đầu tuần từ nhà cung cấp")
                .performer("Trần Văn Kho")
                .build();

        com.phungvanlong.booking_hotel.entity.InventoryTransaction tx2 = com.phungvanlong.booking_hotel.entity.InventoryTransaction.builder()
                .item(item3)
                .type(com.phungvanlong.booking_hotel.entity.InventoryTransactionType.EXPORT)
                .quantity(20)
                .unitPrice(item3.getUnitPrice())
                .totalAmount(item3.getUnitPrice().multiply(BigDecimal.valueOf(20)))
                .reason("Cấp phát cho Housekeeping thay mới buồng phòng")
                .performer("Nguyễn Thị Hoa")
                .build();

        inventoryTransactionRepository.saveAll(Arrays.asList(tx1, tx2));
        log.info("Initialized 2 inventory transactions.");

        // Villa Assets
        com.phungvanlong.booking_hotel.entity.VillaAsset a1 = com.phungvanlong.booking_hotel.entity.VillaAsset.builder()
                .villaNumber("Villa #101")
                .assetName("Smart TV Sony Bravia 65 inch 4K")
                .serialNumber("SN-SONY-65-8891")
                .category("Điện tử")
                .status(com.phungvanlong.booking_hotel.entity.VillaAssetStatus.GOOD)
                .installDate(java.time.LocalDate.now().minusMonths(6))
                .warrantyExpiry(java.time.LocalDate.now().plusMonths(18))
                .note("Lắp tại phòng khách chính")
                .build();

        com.phungvanlong.booking_hotel.entity.VillaAsset a2 = com.phungvanlong.booking_hotel.entity.VillaAsset.builder()
                .villaNumber("Villa #101")
                .assetName("Máy phát tạo ion Ozon khử khuẩn phòng")
                .serialNumber("SN-OZONE-PRO-2026")
                .category("Vệ sinh")
                .status(com.phungvanlong.booking_hotel.entity.VillaAssetStatus.GOOD)
                .installDate(java.time.LocalDate.now().minusMonths(3))
                .warrantyExpiry(java.time.LocalDate.now().plusMonths(9))
                .note("Máy khử trùng Ozon tiêu chuẩn phòng sạch")
                .build();

        com.phungvanlong.booking_hotel.entity.VillaAsset a3 = com.phungvanlong.booking_hotel.entity.VillaAsset.builder()
                .villaNumber("Villa #101")
                .assetName("Tủ lạnh minibar inverter Bosch 90L")
                .serialNumber("SN-BOSCH-90L-4412")
                .category("Gia dụng")
                .status(com.phungvanlong.booking_hotel.entity.VillaAssetStatus.GOOD)
                .installDate(java.time.LocalDate.now().minusMonths(8))
                .warrantyExpiry(java.time.LocalDate.now().plusMonths(16))
                .note("Bảo hành chính hãng Bosch")
                .build();

        villaAssetRepository.saveAll(Arrays.asList(a1, a2, a3));
        log.info("Initialized 3 villa assets.");
    }

    private void initRefillStandardsAndTasks() {
        if (villaSupplyStandardRepository.count() > 0) {
            log.info("Villa supply standards already exist, skipping seed.");
            return;
        }

        List<com.phungvanlong.booking_hotel.entity.Villa> villas = villaRepository.findAll();
        if (villas.isEmpty()) {
            log.info("No villas available to seed supply standards.");
            return;
        }

        com.phungvanlong.booking_hotel.entity.Villa primaryVilla = villas.get(0);
        List<com.phungvanlong.booking_hotel.entity.InventoryItem> items = inventoryItemRepository.findAll();
        if (items.isEmpty()) return;

        List<com.phungvanlong.booking_hotel.entity.VillaSupplyStandard> standards = new ArrayList<>();
        List<com.phungvanlong.booking_hotel.entity.VillaInventory> inventories = new ArrayList<>();

        for (com.phungvanlong.booking_hotel.entity.InventoryItem it : items) {
            int stdQty = switch (it.getCode()) {
                case "AMN-TOOTH-01" -> 4;
                case "AMN-SHAMP-02" -> 2;
                case "LIN-TOWL-03" -> 4;
                case "MINI-WINE-04" -> 2;
                case "CLN-OZONE-05" -> 1;
                default -> 2;
            };

            standards.add(com.phungvanlong.booking_hotel.entity.VillaSupplyStandard.builder()
                    .villa(primaryVilla)
                    .item(it)
                    .standardQuantity(stdQty)
                    .note("Định mức tiêu chuẩn buồng phòng Resort")
                    .build());

            // Tồn kho thực tế ban đầu tại Villa
            inventories.add(com.phungvanlong.booking_hotel.entity.VillaInventory.builder()
                    .villa(primaryVilla)
                    .item(it)
                    .currentQuantity(stdQty)
                    .lastCheckedAt(java.time.LocalDateTime.now())
                    .build());
        }

        villaSupplyStandardRepository.saveAll(standards);
        villaInventoryRepository.saveAll(inventories);
        log.info("Initialized {} supply standards and inventories for villa {}.", standards.size(), primaryVilla.getVillaNumber());

        // Khởi tạo 1 phiếu RefillTask PENDING mẫu sau checkout
        if (refillTaskRepository.count() == 0 && standards.size() >= 3) {
            com.phungvanlong.booking_hotel.entity.RefillTask task = com.phungvanlong.booking_hotel.entity.RefillTask.builder()
                    .taskCode("RF-20260928-0001")
                    .villa(primaryVilla)
                    .status(com.phungvanlong.booking_hotel.entity.RefillTaskStatus.PENDING)
                    .creator("Nguyễn Thị Hoa (Housekeeping)")
                    .assignedStaff("Trần Văn Kho (Thủ kho)")
                    .note("Khách đoàn VIP checkout lúc 11:30. Cần bổ sung vật tư đón khách mới 14:00")
                    .build();

            List<com.phungvanlong.booking_hotel.entity.RefillTaskItem> taskItems = new ArrayList<>();

            // 1. Nước / Bàn chải: định mức 4, thực tế còn 2 => cần refill 2
            var it1 = items.get(0);
            taskItems.add(com.phungvanlong.booking_hotel.entity.RefillTaskItem.builder()
                    .refillTask(task)
                    .item(it1)
                    .standardQuantity(4)
                    .actualQuantity(2)
                    .refillQuantity(2)
                    .consumedQuantity(2)
                    .isFulfilled(false)
                    .build());

            // 2. Dầu gội: định mức 2, thực tế còn 1 => cần refill 1
            var it2 = items.get(1);
            taskItems.add(com.phungvanlong.booking_hotel.entity.RefillTaskItem.builder()
                    .refillTask(task)
                    .item(it2)
                    .standardQuantity(2)
                    .actualQuantity(1)
                    .refillQuantity(1)
                    .consumedQuantity(1)
                    .isFulfilled(false)
                    .build());

            // 3. Khăn tắm: định mức 4, thực tế còn 3 => cần refill 1
            var it3 = items.get(2);
            taskItems.add(com.phungvanlong.booking_hotel.entity.RefillTaskItem.builder()
                    .refillTask(task)
                    .item(it3)
                    .standardQuantity(4)
                    .actualQuantity(3)
                    .refillQuantity(1)
                    .consumedQuantity(1)
                    .isFulfilled(false)
                    .build());

            task.setItems(taskItems);
            refillTaskRepository.save(task);
            log.info("Initialized 1 sample PENDING RefillTask for demonstration.");
        }
    }

    private void initBannersAndZones() {
        // 1. Seed Banners if empty
        if (homeBannerRepository.count() == 0) {
            log.info("Seeding initial home banners...");
            var b1 = com.phungvanlong.booking_hotel.entity.HomeBanner.builder()
                    .title("Nâng Tầm Kỳ Nghỉ Đỉnh Cao")
                    .subtitle("Hệ Thống 12 Điểm Đến Thượng Lưu AURA")
                    .description("Khám phá không gian biệt thự biển biệt lập, hồ bơi riêng và dịch vụ quản gia cao cấp mang đến trải nghiệm nghỉ dưỡng hoàn mỹ.")
                    .imageUrl("/assets/images/rooms/grand-oceanfront.jpg")
                    .mobileImageUrl("/assets/images/rooms/grand-oceanfront.jpg")
                    .ctaText("Khám Phá Biệt Thự")
                    .ctaLink("/villas")
                    .displayOrder(1)
                    .isActive(true)
                    .build();

            var b2 = com.phungvanlong.booking_hotel.entity.HomeBanner.builder()
                    .title("Trải Nghiệm Biệt Thự Biển Sầm Sơn")
                    .subtitle("FLC Sầm Sơn Luxury Resort & Villas")
                    .description("Không gian sang trọng, đón trọn làn gió biển và ánh bình minh rạng rỡ ngay tại các phân khu Ngọc Trai, Sao Biển & San Hô.")
                    .imageUrl("/assets/images/rooms/villa-beachfront.jpg")
                    .mobileImageUrl("/assets/images/rooms/villa-beachfront.jpg")
                    .ctaText("Xem Phân Khu Ngọc Trai")
                    .ctaLink("/villas/zone/villa-ngoc-trai")
                    .displayOrder(2)
                    .isActive(true)
                    .build();

            var b3 = com.phungvanlong.booking_hotel.entity.HomeBanner.builder()
                    .title("Aura Elite Club VIP")
                    .subtitle("Đặc Quyền Nghỉ Dưỡng Thượng Khách")
                    .description("Đặc quyền ưu đãi độc quyền lên tới 25% cùng các tiện ích golf, spa và ẩm thực 5 sao dành riêng cho hội viên cao cấp.")
                    .imageUrl("/assets/images/rooms/royal-penthouse.jpg")
                    .mobileImageUrl("/assets/images/rooms/royal-penthouse.jpg")
                    .ctaText("Nhận Ưu Đãi Ngay")
                    .ctaLink("/promotions")
                    .displayOrder(3)
                    .isActive(true)
                    .build();

            homeBannerRepository.saveAll(List.of(b1, b2, b3));
            log.info("Seeded 3 home banners successfully.");
        }

        // 2. Ensure Zones have slugs, banners, highlights and displayOrder
        log.info("Checking & enhancing zone categories...");
        ensureZone("Ngọc Trai", "villa-ngoc-trai", "NGỌC TRAI", "diamond", "bg-emerald-50 text-emerald-700 border-emerald-200",
                "/assets/images/rooms/villa-beachfront.jpg",
                "Gần bãi biển riêng Sầm Sơn,Hồ bơi vô cực nước ngọt,Quản gia và đầu bếp riêng 24/7,Sân vườn tổ chức tiệc BBQ ngoài trời", 1);

        ensureZone("Sao Biển", "villa-sao-bien", "SAO BIỂN", "star", "bg-amber-50 text-amber-700 border-amber-200",
                "/assets/images/rooms/grand-oceanfront.jpg",
                "Tầm nhìn panorama hướng biển tuyệt mỹ,Nội thất gỗ óc chó cao cấp phong cách Modern Luxury,Bể sục Jacuzzi thư giãn trên ban công,Liền kề trung tâm ẩm thực & sân golf", 2);

        ensureZone("San Hô", "villa-san-ho", "SAN HÔ", "waves", "bg-sky-50 text-sky-700 border-sky-200",
                "/assets/images/rooms/royal-penthouse.jpg",
                "Không gian biệt lập an tĩnh bên rặng dừa xanh,Khu vui chơi trẻ em riêng trong khuôn viên,Phòng chiếu phim & karaoke gia đình công nghệ cao,Sức chứa lớn lý tưởng cho đại gia đình và đoàn teambuilding", 3);
    }

    private void ensureZone(String name, String slug, String tag, String icon, String badgeClass,
                            String bannerUrl, String highlights, int displayOrder) {
        var opt = zoneRepository.findByNameIgnoreCase(name);
        if (opt.isPresent()) {
            var zone = opt.get();
            boolean changed = false;
            if (zone.getSlug() == null || zone.getSlug().isBlank()) {
                zone.setSlug(slug);
                changed = true;
            }
            if (zone.getBannerUrl() == null || zone.getBannerUrl().isBlank()) {
                zone.setBannerUrl(bannerUrl);
                changed = true;
            }
            if (zone.getHighlights() == null || zone.getHighlights().isBlank()) {
                zone.setHighlights(highlights);
                changed = true;
            }
            if (zone.getDisplayOrder() == null) {
                zone.setDisplayOrder(displayOrder);
                changed = true;
            }
            if (changed) {
                zoneRepository.save(zone);
                log.info("Enhanced zone: {}", name);
            }
        } else {
            var newZone = com.phungvanlong.booking_hotel.entity.Zone.builder()
                    .name(name)
                    .matchKey(name.toLowerCase())
                    .slug(slug)
                    .tag(tag)
                    .icon(icon)
                    .badgeClass(badgeClass)
                    .bannerUrl(bannerUrl)
                    .highlights(highlights)
                    .displayOrder(displayOrder)
                    .isActive(true)
                    .description("Phân khu " + name + " tại FLC Sầm Sơn Luxury Resort & Villas")
                    .build();
            zoneRepository.save(newZone);
            log.info("Created new zone: {}", name);
        }
    }

    private void initHousekeepingData() {
        if (housekeepingTaskRepository.count() > 0) {
            log.info("Housekeeping tasks already exist, skipping seed.");
            return;
        }

        var rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            log.warn("No rooms found, skipping housekeeping seed.");
            return;
        }

        User housekeeper = userRepository.findByEmail("hoa.housekeeping@auraholdings.vn").orElse(null);
        User supervisor = userRepository.findByEmail("admin@auraholdings.vn").orElse(null);
        var bookings = bookingRepository.findAll();
        var sampleBooking = bookings.isEmpty() ? null : bookings.get(0);

        var firstRoom = rooms.get(0);
        var secondRoom = rooms.size() > 1 ? rooms.get(1) : firstRoom;
        var firstVilla = firstRoom.getVilla();

        // 1. Task DIRTY with RUSH priority (Checkout Deep Clean)
        firstRoom.setStatus(com.phungvanlong.booking_hotel.entity.RoomStatus.CLEANING);
        roomRepository.save(firstRoom);

        var task1 = com.phungvanlong.booking_hotel.entity.HousekeepingTask.builder()
                .room(firstRoom)
                .villa(firstVilla)
                .housekeeper(housekeeper)
                .supervisor(supervisor)
                .booking(sampleBooking)
                .taskType("CHECKOUT_DEEP")
                .status("PENDING")
                .priority("RUSH")
                .cleaningNote("Khách VIP nhận phòng lúc 14:00. Ưu tiên dọn sâu, bổ sung đầy đủ amenities.")
                .ozoneEnabled(false)
                .build();
        housekeepingTaskRepository.save(task1);

        // 2. Task WAITING_QC with minibar consumption and damaged item
        secondRoom.setStatus(com.phungvanlong.booking_hotel.entity.RoomStatus.CLEANING);
        roomRepository.save(secondRoom);

        var task2 = com.phungvanlong.booking_hotel.entity.HousekeepingTask.builder()
                .room(secondRoom)
                .villa(secondRoom.getVilla())
                .housekeeper(housekeeper)
                .supervisor(supervisor)
                .booking(sampleBooking)
                .taskType("CHECKOUT_DEEP")
                .status("WAITING_QC")
                .priority("NORMAL")
                .ozoneEnabled(true)
                .cleaningNote("Đã dọn xong 16 bước và bật khử khuẩn Ozone 20p. Đã kiểm kê minibar.")
                .checklistJson("[{\"id\":1,\"title\":\"Thay toàn bộ ga trải giường & vỏ gối\",\"category\":\"BEDDING\",\"completed\":true}]")
                .build();
        task2 = housekeepingTaskRepository.save(task2);

        // Add 2 minibar consumptions & 1 asset incident for task2
        var c1 = com.phungvanlong.booking_hotel.entity.RoomConsumptionRecord.builder()
                .housekeepingTask(task2)
                .villa(secondRoom.getVilla())
                .room(secondRoom)
                .booking(sampleBooking)
                .itemType(com.phungvanlong.booking_hotel.entity.RoomConsumptionItemType.MINIBAR_CONSUMED)
                .itemName("Bia Heineken lon 330ml")
                .quantity(2)
                .unitPrice(new BigDecimal("35000"))
                .totalPrice(new BigDecimal("70000"))
                .status(com.phungvanlong.booking_hotel.entity.RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL)
                .recordedBy(housekeeper != null ? housekeeper.getFullName() : "Nguyễn Thị Hoa")
                .build();

        var c2 = com.phungvanlong.booking_hotel.entity.RoomConsumptionRecord.builder()
                .housekeepingTask(task2)
                .villa(secondRoom.getVilla())
                .room(secondRoom)
                .booking(sampleBooking)
                .itemType(com.phungvanlong.booking_hotel.entity.RoomConsumptionItemType.MINIBAR_CONSUMED)
                .itemName("Nước khoáng có gas Perrier 330ml")
                .quantity(1)
                .unitPrice(new BigDecimal("45000"))
                .totalPrice(new BigDecimal("45000"))
                .status(com.phungvanlong.booking_hotel.entity.RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL)
                .recordedBy(housekeeper != null ? housekeeper.getFullName() : "Nguyễn Thị Hoa")
                .build();

        var c3 = com.phungvanlong.booking_hotel.entity.RoomConsumptionRecord.builder()
                .housekeepingTask(task2)
                .villa(secondRoom.getVilla())
                .room(secondRoom)
                .booking(sampleBooking)
                .itemType(com.phungvanlong.booking_hotel.entity.RoomConsumptionItemType.ASSET_DAMAGED)
                .itemName("Tách trà gốm sứ cao cấp Bát Tràng (Sứt mẻ)")
                .quantity(1)
                .unitPrice(new BigDecimal("120000"))
                .totalPrice(new BigDecimal("120000"))
                .status(com.phungvanlong.booking_hotel.entity.RoomConsumptionStatus.PENDING_RECEPTION_APPROVAL)
                .note("Khách làm rơi sứt quai tách trà trên bàn ăn ngoài ban công.")
                .recordedBy(housekeeper != null ? housekeeper.getFullName() : "Nguyễn Thị Hoa")
                .build();

        roomConsumptionRecordRepository.saveAll(Arrays.asList(c1, c2, c3));

        // 3. Lost and Found item
        if (lostAndFoundItemRepository.count() == 0) {
            var lf = com.phungvanlong.booking_hotel.entity.LostAndFoundItem.builder()
                    .itemCode("LF-2026-001")
                    .villa(secondRoom.getVilla())
                    .room(secondRoom)
                    .booking(sampleBooking)
                    .itemName("Ví da Montblanc màu đen")
                    .category("Ví tiền / Giấy tờ")
                    .foundLocation("Dưới gầm giường ngủ Master")
                    .finderName(housekeeper != null ? housekeeper.getFullName() : "Nguyễn Thị Hoa")
                    .guestName("Ông Trần Gia Huy")
                    .guestPhone("(+84) 918 223 999")
                    .status(com.phungvanlong.booking_hotel.entity.LostAndFoundStatus.STORED)
                    .storageLocation("Két sắt an ninh Lễ tân Tầng 1")
                    .note("Bên trong có CCCD và thẻ ngân hàng.")
                    .build();
            lostAndFoundItemRepository.save(lf);
        }

        // 4. Maintenance ticket
        if (maintenanceTicketRepository.count() == 0) {
            var mt = com.phungvanlong.booking_hotel.entity.MaintenanceTicket.builder()
                    .ticketCode("MT-2026-001")
                    .villa(firstVilla)
                    .room(firstRoom)
                    .category("Điều hòa & Điện lạnh")
                    .priority(com.phungvanlong.booking_hotel.entity.MaintenancePriority.HIGH)
                    .status(com.phungvanlong.booking_hotel.entity.MaintenanceStatus.REPORTED)
                    .description("Điều hòa phòng khách Daikin Inverter kêu rè và không phả hơi lạnh.")
                    .reportedBy(housekeeper != null ? housekeeper.getFullName() : "Nguyễn Thị Hoa")
                    .build();
            maintenanceTicketRepository.save(mt);
        }

        log.info("Initialized Housekeeping ecosystem operational sample data.");
    }
}

