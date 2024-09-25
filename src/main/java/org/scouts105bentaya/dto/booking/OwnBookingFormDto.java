package org.scouts105bentaya.dto.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OwnBookingFormDto {
    @Positive
    @NotNull
    private Integer packs;

    @NotNull
    private ScoutCenter scoutCenter;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Length(max = 1023)
    @NotBlank
    private String observations;

    private boolean exclusiveReservation = false;
}
