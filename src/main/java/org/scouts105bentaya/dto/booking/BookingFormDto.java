package org.scouts105bentaya.dto.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;

public record BookingFormDto(
    @Length(max = 255) @NotBlank String groupName,
    @Length(max = 255) @NotBlank String cif,
    @Length(max = 511) @NotBlank String workDescription,
    @Length(max = 255) @NotBlank String contactName,
    @Length(max = 255) @NotBlank String relationship,
    @Length(max = 255) @NotBlank @Email String email,
    @Length(max = 255) @NotBlank String phone,
    @Positive @NotNull Integer packs,
    @NotNull ScoutCenter scoutCenter,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    @Length(max = 1023) String observations,
    boolean exclusiveReservation
) {
}
