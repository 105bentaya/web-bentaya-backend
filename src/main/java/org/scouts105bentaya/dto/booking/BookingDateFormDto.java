package org.scouts105bentaya.dto.booking;

import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;

public record BookingDateFormDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    ScoutCenter scoutCenter
) {
}