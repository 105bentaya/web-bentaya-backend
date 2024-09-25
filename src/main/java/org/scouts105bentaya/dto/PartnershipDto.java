package org.scouts105bentaya.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class PartnershipDto {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String phone;

    @NotNull
    @Length(max = 600)
    private String subject;

    private String entityName;

    @NotNull
    private String message;

}
