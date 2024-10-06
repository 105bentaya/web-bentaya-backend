package org.scouts105bentaya.dto.attendance;

public record AttendanceScoutEventInfo(
    Boolean attending,
    String name,
    Integer scoutId,
    Boolean payed
) {
}
