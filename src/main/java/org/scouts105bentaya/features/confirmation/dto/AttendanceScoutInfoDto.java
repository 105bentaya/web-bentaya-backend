package org.scouts105bentaya.features.confirmation.dto;

import java.time.ZonedDateTime;

public record AttendanceScoutInfoDto(
    Integer eventId,
    ZonedDateTime eventStartDate,
    ZonedDateTime eventEndDate,
    String eventTitle,
    Boolean attending,
    Boolean payed,
    boolean closed,
    boolean endsSoon
) {
}
