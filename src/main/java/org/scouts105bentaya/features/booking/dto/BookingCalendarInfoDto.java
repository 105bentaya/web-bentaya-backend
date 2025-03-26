package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingCalendarInfoDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    BookingStatus status,
    Boolean fullyOccupied,
    Integer id,
    Integer packs
) {
    public static BookingCalendarInfoDto fromBooking(Booking booking) {
        return new BookingCalendarInfoDto(
            booking.getStartDate(),
            booking.getEndDate(),
            booking.getStatus(),
            booking.getStatus() == BookingStatus.OCCUPIED ? booking.isExclusiveReservation() : null,
            booking.getId(),
            booking.getPacks()
        );
    }
}
