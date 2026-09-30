package com.phungvanlong.booking_hotel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Find unique indexes on villa_number
            List<String> indexes = jdbcTemplate.queryForList(
                "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = 'hotel_booking_db' AND TABLE_NAME = 'villas' AND COLUMN_NAME = 'villa_number' AND NON_UNIQUE = 0", 
                String.class);

            for (String indexName : indexes) {
                if (!indexName.contains("zone")) {
                    jdbcTemplate.execute("ALTER TABLE villas DROP INDEX " + indexName);
                    System.out.println("Dropped index on villas: " + indexName);
                }
            }

            // Find unique indexes on room_number in rooms table
            List<String> roomIndexes = jdbcTemplate.queryForList(
                "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = 'hotel_booking_db' AND TABLE_NAME = 'rooms' AND COLUMN_NAME = 'room_number' AND NON_UNIQUE = 0", 
                String.class);

            for (String indexName : roomIndexes) {
                if (!indexName.contains("villa") && !indexName.contains("zone")) {
                    jdbcTemplate.execute("ALTER TABLE rooms DROP INDEX " + indexName);
                    System.out.println("Dropped index on rooms: " + indexName);
                }
            }

            try {
                jdbcTemplate.execute("ALTER TABLE home_banners MODIFY COLUMN image_url LONGTEXT NOT NULL");
                jdbcTemplate.execute("ALTER TABLE home_banners MODIFY COLUMN mobile_image_url LONGTEXT NULL");
                jdbcTemplate.execute("ALTER TABLE zones MODIFY COLUMN banner_url LONGTEXT NULL");
                System.out.println("Successfully ensured image columns are LONGTEXT in home_banners & zones");
            } catch (Exception ex) {
                System.out.println("Notice on altering image columns: " + ex.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
