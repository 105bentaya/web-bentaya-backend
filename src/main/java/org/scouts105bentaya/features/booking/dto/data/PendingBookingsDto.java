package org.scouts105bentaya.features.booking.dto.data;

import java.util.List;

public record PendingBookingsDto(
    List<BookingInfoDto> newBookings,
    List<BookingInfoDto> acceptedBookings,
    List<BookingInfoDto> confirmedBookings,
    List<BookingInfoDto> finishedBookings
) {
}
