package org.scouts105bentaya.dto.attendance;

import java.time.ZonedDateTime;

public record AttendanceScoutInfoDto(
    Integer eventId,
    ZonedDateTime eventStartDate,
    ZonedDateTime eventEndDate,
    String eventTitle,
    Boolean attending,
    Boolean payed,
    boolean closed
) {
}
