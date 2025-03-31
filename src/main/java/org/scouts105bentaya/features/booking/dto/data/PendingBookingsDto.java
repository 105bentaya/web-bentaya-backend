package org.scouts105bentaya.features.booking.dto.data;

import org.scouts105bentaya.features.booking.dto.BookingDto;

import java.util.List;

public record PendingBookingsDto(
    List<BookingDto> newBookings,
    List<BookingDto> acceptedBookings,
    List<BookingDto> confirmedBookings,
    List<BookingDto> finishedBookings
) {
}
