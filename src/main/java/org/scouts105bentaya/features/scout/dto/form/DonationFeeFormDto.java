package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DonationFeeFormDto(
    @NotNull LocalDate issueDate,
    @NotNull LocalDate dueDate,
    @NotNull String description,
    Integer amount,
    @NotNull Integer donationTypeId,
    @NotNull String account,
    boolean applyToCurrentScouts
) {
}
