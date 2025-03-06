package org.scouts105bentaya.features.event.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;

import java.time.ZonedDateTime;

public record EventDto(
    Integer id,
    GroupBasicDataDto group,
    boolean forScouters,
    boolean forEveryone,
    String title,
    String description,
    String location,
    String longitude,
    String latitude,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    boolean unknownTime,
    Boolean hasAttendance,
    boolean attendanceIsClosed,
    ZonedDateTime closeDateTime
) {
}
