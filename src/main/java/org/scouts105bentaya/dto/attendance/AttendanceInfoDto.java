package org.scouts105bentaya.dto.attendance;

public record AttendanceInfoDto(
    String name,
    String surname,
    Integer scoutId,
    Boolean attending,
    Boolean payed,
    String text,
    String medicalData
) {
}
