package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.ScoutCenter;

import java.time.LocalDateTime;

public record BookingDateFormDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    ScoutCenter scoutCenter
) {
}