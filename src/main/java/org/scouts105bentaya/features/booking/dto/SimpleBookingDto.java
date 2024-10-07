package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.enums.BookingStatus;

import java.time.LocalDateTime;

public record SimpleBookingDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    BookingStatus status
) {
}
