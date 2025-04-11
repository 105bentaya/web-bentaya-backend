package org.scouts105bentaya.features.booking.dto.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record BookingUpdateDto(
    @Length(max = 255) String groupName,
    @Length(max = 255) @Pattern(regexp = "[A-Z0-9]+") String cif,
    @Positive @NotNull Integer packs,
    @NotNull Integer scoutCenterId,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    boolean exclusiveReservation,
    Float price
) {
}
