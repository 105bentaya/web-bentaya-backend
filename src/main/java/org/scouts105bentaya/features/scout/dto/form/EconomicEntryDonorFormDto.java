package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.features.scout.enums.PersonType;

public record EconomicEntryDonorFormDto(
    @NotNull String name,
    String surname,
    @NotNull IdDocumentFormDto idDocument,
    @NotNull PersonType personType
) {
}
