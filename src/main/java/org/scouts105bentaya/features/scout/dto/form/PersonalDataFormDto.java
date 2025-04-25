package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.scout.enums.PersonType;

public record PersonalDataFormDto(
    @NotNull PersonType type,
    @Valid IdDocumentFormDto idDocument,
    @Length(max = 65535) String observations,
    @Valid RealPersonalDataFormDto realData,
    @Valid JuridicalPersonalDataFormDto juridicalData
) {
}
