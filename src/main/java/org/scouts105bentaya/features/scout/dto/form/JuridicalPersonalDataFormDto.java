package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record JuridicalPersonalDataFormDto(
    @NotNull @Length(max = 255) String companyName,
    @Valid JuridicalRepresentativeFormDto juridicalRepresentative
) {
    public record JuridicalRepresentativeFormDto(
        @NotNull @Length(max = 255) String name,
        @Length(max = 255) String surname,
        @Email @Length(max = 255) String email,
        @Length(max = 255) String phone,
        @Length(max = 255) String landline,
        @Valid IdDocumentFormDto idDocument
    ) {
    }
}
