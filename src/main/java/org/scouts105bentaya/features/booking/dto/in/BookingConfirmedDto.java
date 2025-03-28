package org.scouts105bentaya.features.booking.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingConfirmedDto extends BookingStatusUpdateDto {
    @NotNull
    private Boolean exclusive;
}
