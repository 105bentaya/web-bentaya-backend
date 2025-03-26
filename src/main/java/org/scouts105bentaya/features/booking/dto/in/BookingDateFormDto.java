package org.scouts105bentaya.features.booking.dto.in;

import java.time.LocalDateTime;

public record BookingDateFormDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer scoutCenterId
) {
}