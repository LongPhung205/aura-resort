package com.phungvanlong.booking_hotel.cron;

import com.phungvanlong.booking_hotel.entity.HousekeepingTask;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import com.phungvanlong.booking_hotel.repository.HousekeepingTaskRepository;
import com.phungvanlong.booking_hotel.repository.VillaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyHousekeepingJob {

    private final VillaRepository villaRepository;
    private final HousekeepingTaskRepository housekeepingTaskRepository;

    // Chạy mỗi ngày lúc 6:00 sáng
    @Scheduled(cron = "0 0 6 * * *")
    public void generateDailyCleaningTasks() {
        log.info("Bắt đầu tự động tạo nhiệm vụ dọn phòng hàng ngày (DAILY)...");
        
        List<Villa> occupiedVillas = villaRepository.findByStatus(VillaStatus.OCCUPIED);
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        
        int createdCount = 0;
        
        for (Villa villa : occupiedVillas) {
            boolean alreadyHasToday = housekeepingTaskRepository
                    .existsByVillaIdAndTaskTypeAndCreatedAtAfter(villa.getId(), "DAILY", startOfToday);
                    
            if (!alreadyHasToday) {
                HousekeepingTask task = HousekeepingTask.builder()
                        .villa(villa)
                        .taskType("DAILY")
                        .status("PENDING")
                        .priority("NORMAL")
                        .build();
                housekeepingTaskRepository.save(task);
                createdCount++;
            }
        }
        
        log.info("Hoàn thành tạo {} nhiệm vụ dọn phòng DAILY.", createdCount);
    }
}
