package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.enums.BookingDocumentDuration;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.user.User;

import java.time.LocalDate;

public record BookingDocumentDto(
    Integer id,
    Integer bookingId,
    String fileName,
    BookingDocumentStatus status,
    Integer typeId,
    BookingDocumentDuration duration,
    LocalDate expirationDate,
    boolean ownedByUser
) {
    public static BookingDocumentDto fromBookingDocument(BookingDocument bookingDocument) {
        return new BookingDocumentDto(
            bookingDocument.getId(),
            bookingDocument.getBooking().getId(),
            bookingDocument.getFile().getName(),
            bookingDocument.getStatus(),
            bookingDocument.getType().getId(),
            bookingDocument.getDuration(),
            bookingDocument.getExpirationDate(),
            true
        );
    }
    public static BookingDocumentDto fromBookingDocumentAndUser(BookingDocument bookingDocument, User user) {
        boolean ownedByUser = bookingDocument.getFile().getUser().getId().equals(user.getId());
        return new BookingDocumentDto(
            bookingDocument.getId(),
            bookingDocument.getBooking().getId(),
            ownedByUser ? bookingDocument.getFile().getName() : "documento",
            bookingDocument.getStatus(),
            bookingDocument.getType().getId(),
            bookingDocument.getDuration(),
            bookingDocument.getExpirationDate(),
            ownedByUser
        );
    }
}
