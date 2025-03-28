package org.scouts105bentaya.features.booking.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class BookingWarningDto extends BookingStatusUpdateDto {
    @NotNull
    @Length(max = 63)
    private String subject;
}
