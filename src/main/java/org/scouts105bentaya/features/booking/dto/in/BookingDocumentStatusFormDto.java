package org.scouts105bentaya.features.booking.dto.in;

import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.features.booking.enums.BookingDocumentDuration;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;

import java.time.LocalDate;

public record BookingDocumentStatusFormDto(
    @NotNull BookingDocumentStatus status,
    BookingDocumentDuration duration,
    LocalDate expirationDate
) {
}