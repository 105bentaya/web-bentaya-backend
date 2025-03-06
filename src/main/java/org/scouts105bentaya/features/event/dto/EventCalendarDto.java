package org.scouts105bentaya.features.event.dto;

import java.time.ZonedDateTime;

public record EventCalendarDto(
    Integer id,
    Integer groupId,
    String title,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    boolean unknownTime,
    boolean forEveryone,
    boolean forScouters
) {
}
