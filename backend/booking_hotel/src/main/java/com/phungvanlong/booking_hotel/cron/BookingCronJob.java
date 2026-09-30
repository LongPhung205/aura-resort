package com.phungvanlong.booking_hotel.cron;

import com.phungvanlong.booking_hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCronJob {

    private final BookingService bookingService;

    // Chạy mỗi 1 phút
    @Scheduled(cron = "0 * * * * *")
    public void cancelExpiredBookings() {
        bookingService.cancelExpiredBookings();
        log.info("Cron Job executed: Checked and cancelled expired bookings");
    }
}
