package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.scout.enums.ScoutType;

import java.time.LocalDate;
import java.util.List;

public record NewScoutFormDto(
    @Valid IdDocumentFormDto idDocument,
    @NotNull @Length(max = 255) String surname,
    @NotNull @Length(max = 255) String name,
    @Length(max = 255) String feltName,
    @NotNull LocalDate birthday,
    @Length(max = 255) String address,
    @Length(max = 255) String city,
    @Length(max = 255) String province,
    @Length(max = 255) String phone,
    @Length(max = 255) String landline,
    @Email @Length(max = 255) String email,
    @Length(max = 255) String shirtSize,
    @Length(max = 255) String residenceMunicipality,
    @NotNull @Length(max = 255) String gender,
    @NotNull Boolean imageAuthorization,
    @Valid ContactFormDto contact,
    @Length(max = 255) String iban,
    @Length(max = 255) String bank,
    @NotNull ScoutType scoutType,
    Integer groupId,
    Integer census,
    @NotNull LocalDate firstActivityDate,
    @NotNull List<String> scoutUsers,
    Integer preScoutId
) {
}
