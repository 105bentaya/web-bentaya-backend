package org.scouts105bentaya.dto.event;

import java.time.ZonedDateTime;

public record EventCalendarDto(
    Integer id,
    Integer groupId,
    String title,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    boolean unknownTime
) {
}
