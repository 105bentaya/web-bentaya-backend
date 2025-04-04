package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;

public record BookingDocumentDto(
    Integer id,
    Integer bookingId,
    String fileName,
    BookingDocumentStatus status,
    Integer typeId
) {
    public static BookingDocumentDto fromBooking(BookingDocument bookingDocument) {
        return new BookingDocumentDto(
            bookingDocument.getId(),
            bookingDocument.getBooking().getId(),
            bookingDocument.getFile().getName(),
            bookingDocument.getStatus(),
            bookingDocument.getType().getId()
        );
    }
}
