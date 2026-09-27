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
}

