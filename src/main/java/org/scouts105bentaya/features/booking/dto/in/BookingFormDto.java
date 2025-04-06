package org.scouts105bentaya.features.booking.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record BookingFormDto(
    @Length(max = 255) @NotBlank String groupName,
    @Length(max = 255) @NotBlank @Pattern(regexp = "[A-Z0-9]+") String cif,
    @Length(max = 511) @NotBlank String groupDescription,
    @Length(max = 2000) @NotBlank String facilityUse,
    @Length(max = 255) @NotBlank String contactName,
    @Length(max = 255) @NotBlank String relationship,
    @Length(max = 255) @NotBlank @Email String email,
    @Length(max = 255) @NotBlank String phone,
    @Positive @NotNull Integer packs,
    @NotNull Integer scoutCenterId,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    @Length(max = 1023) String observations,
    boolean exclusiveReservation
) {
}
