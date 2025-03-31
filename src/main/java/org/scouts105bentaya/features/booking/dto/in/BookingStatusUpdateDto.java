package org.scouts105bentaya.features.booking.dto.in;

import jakarta.annotation.Nullable;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

@Setter
public class BookingStatusUpdateDto {

    @Length(max = 2047)
    private String observations;

    public @Nullable String getObservations() {
        return StringUtils.isBlank(observations) ? null : observations;
    }
}
