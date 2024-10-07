package org.scouts105bentaya.dto.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;

public record OwnBookingFormDto(
    @Positive @NotNull Integer packs,
    @NotNull ScoutCenter scoutCenter,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    @Length(max = 1023) @NotBlank String observations,
    boolean exclusiveReservation
) {
}
