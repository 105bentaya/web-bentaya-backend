package org.scouts105bentaya.dto;


import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record PartnershipDto(
    @NotNull String name,
    @NotNull String email,
    @NotNull String phone,
    @NotNull @Length(max = 600) String subject,
    String entityName,
    @NotNull String message
) {
}
