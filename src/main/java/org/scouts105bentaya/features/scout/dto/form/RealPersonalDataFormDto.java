package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record RealPersonalDataFormDto(
    @NotNull @Length(max = 255) String surname,
    @NotNull @Length(max = 255) String name,
    @Length(max = 255) String feltName,
    @NotNull LocalDate birthday,
    @Length(max = 255) String birthplace,
    @Length(max = 255) String birthProvince,
    @Length(max = 255) String nationality,
    @Length(max = 255) String address,
    @Length(max = 255) String city,
    @Length(max = 255) String province,
    @Length(max = 255) String phone,
    @Length(max = 255) String landline,
    @Email @Length(max = 255) String email,
    @Length(max = 255) String shirtSize,
    @Length(max = 255) String residenceMunicipality,
    @NotNull @Length(max = 255) String gender
) {
}
