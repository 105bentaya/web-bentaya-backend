package org.scouts105bentaya.features.booking.converter;

import org.scouts105bentaya.features.booking.dto.BookingDocumentDto;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class BookingDocumentConverter extends GenericConverter<BookingDocument, BookingDocumentDto> {

    @Override
    public BookingDocument convertFromDto(BookingDocumentDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public BookingDocumentDto convertFromEntity(BookingDocument entity) {
        return new BookingDocumentDto(
            entity.getId(),
            entity.getBooking().getId(),
            entity.getFileName(),
            entity.getStatus()
        );
    }
}
