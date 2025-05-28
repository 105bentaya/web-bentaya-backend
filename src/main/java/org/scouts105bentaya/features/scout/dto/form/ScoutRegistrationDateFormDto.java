package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScoutRegistrationDateFormDto(
    Integer id,
    @NotNull LocalDate registrationDate,
    LocalDate unregistrationDate
) {
}
