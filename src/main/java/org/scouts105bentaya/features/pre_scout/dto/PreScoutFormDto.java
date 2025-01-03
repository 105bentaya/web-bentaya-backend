package org.scouts105bentaya.features.pre_scout.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import javax.annotation.Nullable;

public record PreScoutFormDto(
    @NotNull String name,
    @NotNull String firstSurname,
    @Nullable String secondSurname,
    @NotNull String birthday,
    @NotNull String gender,
    @NotNull String dni,
    @NotNull String size,
    @NotNull @Length(max = 512) String medicalData,
    boolean hasBeenInGroup,
    @Nullable String yearAndSection,
    @NotNull String parentsName,
    @NotNull String parentsFirstSurname,
    @Nullable String parentsSecondSurname,
    @NotNull String relationship,
    @NotNull String phone,
    @NotNull String email,
    @NotNull @Length(max = 256) String comment,
    @NotNull Integer priority,
    @Nullable String priorityInfo
) {
}
