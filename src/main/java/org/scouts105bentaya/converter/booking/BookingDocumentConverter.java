package org.scouts105bentaya.converter.booking;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.booking.BookingDocumentDto;
import org.scouts105bentaya.entity.BookingDocument;
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
