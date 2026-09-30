package com.phungvanlong.booking_hotel.cron;

import com.phungvanlong.booking_hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingCronJob {

    private final BookingService bookingService;

    // Chạy mỗi 1 phút
    @Scheduled(cron = "0 * * * * *")
    public void cancelExpiredBookings() {
        bookingService.cancelExpiredBookings();
        System.out.println("Cron Job executed: Checked and cancelled expired bookings");
    }
}
