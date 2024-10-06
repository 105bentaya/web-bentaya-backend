package org.scouts105bentaya.dto.booking;

import org.scouts105bentaya.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingDateDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    BookingStatus status,
    Integer id,
    Integer packs
) {
}
