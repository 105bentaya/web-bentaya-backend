package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record EconomicEntryFormDto(
    @NotNull LocalDate date,
    @NotNull String description,
    @NotNull int amount,
    String income,
    String spending,
    String account,
    @NotNull String type,
    @Length(max = 511) String observations
) {
}
