package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingDateDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    BookingStatus status,
    Integer id,
    Integer packs
) {
}
