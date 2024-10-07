package org.scouts105bentaya.dto.booking;

import org.scouts105bentaya.enums.BookingStatus;

import java.time.LocalDateTime;

public record SimpleBookingDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    BookingStatus status
) {
}
