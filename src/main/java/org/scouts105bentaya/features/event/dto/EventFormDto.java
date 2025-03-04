package org.scouts105bentaya.features.event.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record EventFormDto(
    Integer id,
    Integer groupId,
    String title,
    String description,
    String location,
    String longitude,
    String latitude,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    //todo maybe it can be removed and use withzonesamelocal
    LocalDate localStartDate,
    LocalDate localEndDate,
    boolean unknownTime,
    @NotNull(message = "Activating Attendance List") boolean activateAttendanceList,
    boolean activateAttendancePayment,
    boolean closeAttendanceList,
    @Nullable ZonedDateTime closeDateTime
) {
}
