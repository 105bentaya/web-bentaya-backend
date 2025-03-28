package org.scouts105bentaya.features.booking.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingAcceptedDto extends BookingStatusUpdateDto {
    @NotNull
    private Float price;
}
