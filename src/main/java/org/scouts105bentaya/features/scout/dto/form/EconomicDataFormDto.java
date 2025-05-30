package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record EconomicDataFormDto(
    Integer donorId,
    @NotNull @Length(max = 255) String iban,
    @NotNull @Length(max = 255) String bank
) {
}
