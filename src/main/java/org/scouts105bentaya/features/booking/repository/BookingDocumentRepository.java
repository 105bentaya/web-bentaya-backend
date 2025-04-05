package org.scouts105bentaya.features.booking.repository;


import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Integer> {
    default BookingDocument get(Integer id) {
        return this.findById(id).orElseThrow(() -> new WebBentayaBadRequestException("Booking Document not found"));
    }

    List<BookingDocument> findAllByBookingId(Integer bookingId);

    @Query("""
        SELECT b FROM BookingDocument b
        WHERE b.id IN (
            SELECT MAX(bd.id) FROM BookingDocument bd
            WHERE bd.booking.cif = :cif
              AND bd.status = 'ACCEPTED'
              AND bd.type.active = true
              AND bd.duration <> 'SINGLE_USE'
            GROUP BY bd.file.id
        ) AND b.duration = 'PERMANENT'
          OR (b.duration = 'EXPIRABLE' AND b.expirationDate > :bookingEndDate)
        """)
    List<BookingDocument> findUserBookingValidDocuments(String cif, LocalDate bookingEndDate);
}
