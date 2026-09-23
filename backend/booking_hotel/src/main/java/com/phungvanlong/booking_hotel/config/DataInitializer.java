package com.phungvanlong.booking_hotel.config;

import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

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
}
