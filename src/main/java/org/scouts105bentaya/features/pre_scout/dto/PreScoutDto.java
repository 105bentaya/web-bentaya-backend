package org.scouts105bentaya.features.pre_scout.dto;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record PreScoutDto(
    Integer id,
    @NotNull String name,
    @NotNull String surname,
    String section,
    @NotNull String birthday,
    String age,
    @NotNull String gender,
    @NotNull String dni,
    boolean hasBeenInGroup,
    String yearAndSection,
    @NotNull String medicalData,
    @NotNull String parentsName,
    @NotNull String parentsSurname,
    @NotNull String relationship,
    @NotNull String phone,
    @NotNull String email,
    String comment,
    @NotNull Integer priority,
    String priorityInfo,
    ZonedDateTime creationDate,
    Integer status,
    Integer groupId,
    String assignationComment,
    ZonedDateTime assignationDate,
    int inscriptionYear,
    @NotNull String size
) {
}
