package org.scouts105bentaya.features.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.booking.enums.BookingStatus;

@Setter
public class BookingStatusUpdateDto {

    @Getter
    @NotNull
    @Positive
    private Integer id;

    @Getter
    @NotNull
    private BookingStatus newStatus;

    @Length(max = 2047)
    private String observations;

    @Getter
    private Float price;

    public String getObservations() {
        if (observations != null && observations.isBlank()) {
            return null;
        }
        return observations;
    }
}
