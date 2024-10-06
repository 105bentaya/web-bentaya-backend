package org.scouts105bentaya.dto.attendance;

import java.util.List;

public record AttendanceListUserDto(
    Integer scoutId,
    String name,
    String surname,
    List<AttendanceScoutInfoDto> info
) {
}
