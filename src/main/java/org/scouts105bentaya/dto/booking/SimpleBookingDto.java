package org.scouts105bentaya.dto.booking;

import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.enums.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class SimpleBookingDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BookingStatus status;
}
