package org.scouts105bentaya.features.event.dto;

import org.springframework.http.HttpStatus;

public record CalendarDto(
    byte[] calendar,
    HttpStatus status
) {
}
