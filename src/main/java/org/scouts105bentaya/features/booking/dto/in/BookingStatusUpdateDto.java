package org.scouts105bentaya.features.booking.dto.in;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class BookingStatusUpdateDto {

    @Getter(AccessLevel.NONE)
    @Length(max = 2047)
    private String observations;

    public @Nullable String getObservations() {
        return StringUtils.isBlank(observations) ? null : observations;
    }
}
