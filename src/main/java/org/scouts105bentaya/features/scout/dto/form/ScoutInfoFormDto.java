package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.features.scout.enums.ScoutType;

import java.util.List;

public record ScoutInfoFormDto(
    @NotNull ScoutType scoutType,
    Integer groupId,
    @NotNull @Valid List<ScoutRegistrationDateFormDto> registrationDates,
    @NotNull boolean federated,
    Integer census
) {
}
