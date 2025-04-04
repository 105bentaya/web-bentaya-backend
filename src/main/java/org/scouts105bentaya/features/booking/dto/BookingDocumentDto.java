package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.enums.BookingDocumentDuration;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;

import java.time.LocalDate;

public record BookingDocumentDto(
    Integer id,
    Integer bookingId,
    String fileName,
    BookingDocumentStatus status,
    Integer typeId,
    BookingDocumentDuration duration,
    LocalDate expirationDate
) {
    public static BookingDocumentDto fromBookingDocument(BookingDocument bookingDocument) {
        return new BookingDocumentDto(
            bookingDocument.getId(),
            bookingDocument.getBooking().getId(),
            bookingDocument.getFile().getName(),
            bookingDocument.getStatus(),
            bookingDocument.getType().getId(),
            bookingDocument.getDuration(),
            bookingDocument.getExpirationDate()
        );
    }
}
