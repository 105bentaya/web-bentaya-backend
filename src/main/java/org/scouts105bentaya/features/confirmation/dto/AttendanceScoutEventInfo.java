package org.scouts105bentaya.features.confirmation.dto;

public record AttendanceScoutEventInfo(
    Boolean attending,
    String name,
    Integer scoutId,
    Boolean payed
) {
}
