package org.scouts105bentaya.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class SeniorFormDto {

    private Integer id;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String name;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String surname;

    @NotNull
    @NotBlank
    @Length(max = 511)
    private String email;

    @NotNull
    @NotBlank
    @Length(max = 63)
    private String phone;

    @NotNull
    private Boolean acceptMessageGroup;

    @NotNull
    private Boolean acceptNewsletter;

    @Length(max = 511)
    private String observations;
}
