package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record EconomicEntryFormDto(
    @NotNull LocalDate issueDate,
    @NotNull LocalDate dueDate,
    @NotNull String description,
    @NotNull int amount,
    Integer incomeId,
    Integer expenseId,
    String account,
    @NotNull String type,
    @Length(max = 511) String observations
) {
}
