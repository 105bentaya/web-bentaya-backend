package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;

public record BookingDocumentDto(
    Integer id,
    Integer bookingId,
    String fileName,
    BookingDocumentStatus status
) {
}
