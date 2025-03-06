package org.scouts105bentaya.features.event.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record EventDateConflictsFormDto(
    @NotNull ZonedDateTime startDate,
    @NotNull ZonedDateTime endDate,
    @Nullable Integer groupId
) {
}
