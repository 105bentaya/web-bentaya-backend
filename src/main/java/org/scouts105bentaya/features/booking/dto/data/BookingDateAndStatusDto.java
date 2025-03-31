package org.scouts105bentaya.features.booking.dto.data;

import org.scouts105bentaya.features.booking.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingDateAndStatusDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    BookingStatus status,
    Boolean fullyOccupied
) {
}
