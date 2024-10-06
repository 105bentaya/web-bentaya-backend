package org.scouts105bentaya.dto.booking;

import org.scouts105bentaya.enums.BookingDocumentStatus;

public record BookingDocumentDto(
    Integer id,
    Integer bookingId,
    String fileName,
    BookingDocumentStatus status
) {
}
