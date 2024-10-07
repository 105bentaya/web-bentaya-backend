package org.scouts105bentaya.features.confirmation.dto;

import java.util.List;

public record AttendanceListUserDto(
    Integer scoutId,
    String name,
    String surname,
    List<AttendanceScoutInfoDto> info
) {
}
