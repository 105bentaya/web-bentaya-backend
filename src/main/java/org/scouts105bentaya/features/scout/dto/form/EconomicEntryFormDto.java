package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.scout.enums.EntryType;

import java.time.LocalDate;

public record EconomicEntryFormDto(
    @NotNull LocalDate issueDate,
    @NotNull LocalDate dueDate,
    @NotNull String description,
    @NotNull int amount,
    Integer incomeId,
    Integer expenseId,
    String account,
    @NotNull EntryType type,
    @Length(max = 511) String observations,
    @Valid EconomicEntryDonorFormDto donor
) {
}
