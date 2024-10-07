package org.scouts105bentaya.features.senior_section;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record SeniorFormDto(
    Integer id,
    @NotNull @NotBlank @Length(max = 255) String name,
    @NotNull @NotBlank @Length(max = 255) String surname,
    @NotNull @NotBlank @Length(max = 511) String email,
    @NotNull @NotBlank @Length(max = 63) String phone,
    @NotNull Boolean acceptMessageGroup,
    @NotNull Boolean acceptNewsletter,
    @Length(max = 511) String observations
) {
}
