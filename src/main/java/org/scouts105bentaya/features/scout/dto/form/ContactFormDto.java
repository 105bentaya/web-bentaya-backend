package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.scout.enums.PersonType;

import javax.annotation.Nullable;

public record ContactFormDto(
    @Nullable Integer id,
    @NotNull PersonType personType,
    @Length(max = 255) String companyName,
    @Length(max = 255) @NotNull String name,
    @Length(max = 255) String surname,
    @Length(max = 255) String relationship,
    @NotNull boolean donor,
    @Valid IdDocumentFormDto idDocument,
    @Length(max = 255) String phone,
    @Length(max = 255) @Email String email,
    @Length(max = 255) String studies,
    @Length(max = 255) String profession,
    @Length(max = 65535) String observations
) {
}
