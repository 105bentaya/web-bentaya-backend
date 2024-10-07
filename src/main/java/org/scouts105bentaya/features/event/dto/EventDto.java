package org.scouts105bentaya.features.event.dto;

import java.time.ZonedDateTime;

public record EventDto(
    Integer id,
    Integer groupId,
    String title,
    String description,
    String location,
    String longitude,
    String latitude,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    boolean unknownTime,
    Boolean hasAttendance
) {
}
