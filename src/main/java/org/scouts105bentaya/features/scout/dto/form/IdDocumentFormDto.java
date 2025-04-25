package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.scout.enums.IdType;

public record IdDocumentFormDto(
    @NotNull IdType idType,
    @NotNull @Length(max = 255) String number
) {
}
