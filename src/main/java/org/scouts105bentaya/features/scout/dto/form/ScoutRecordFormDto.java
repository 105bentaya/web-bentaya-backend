package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.shared.GenericConstants;

import java.time.LocalDate;

public record ScoutRecordFormDto(
    @NotNull String recordType,
    LocalDate startDate,
    LocalDate endDate,
    @NotBlank @Length(max = GenericConstants.MYSQL_TEXT_LENGTH) String observations
) {
}