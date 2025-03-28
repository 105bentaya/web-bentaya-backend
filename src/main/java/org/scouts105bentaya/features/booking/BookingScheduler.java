package org.scouts105bentaya.features.booking;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.service.BookingStatusService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingScheduler {

    private final BookingStatusService bookingStatusService;

    public BookingScheduler(BookingStatusService bookingStatusService) {
        this.bookingStatusService = bookingStatusService;
    }

    @Scheduled(cron = "0 0 10,12,16,18 * * ?", zone = "Atlantic/Canary")
    private void checkForFinishedBookings() {
        log.info("Checking for finished bookings");
        bookingStatusService.checkForFinishedBookings();
        log.info("Check complete");
    }
}
