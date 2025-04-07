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

    @Query(value = """
        SELECT *
          FROM booking_document b
          WHERE b.id IN (
              SELECT MAX(bd.id)
              FROM booking_document bd
               JOIN general_booking gb ON bd.booking_id = gb.id
               JOIN booking_document_file f ON bd.file_id = f.id
               JOIN booking_document_type dt ON bd.type_id = dt.id
              WHERE gb.cif = :cif
                AND dt.active = TRUE
              GROUP BY bd.file_id
          )
            AND (
              b.duration = 'PERMANENT'
              OR (b.duration = 'EXPIRABLE' AND b.expiration_date > :bookingEndDate)
              )
            AND b.status = 'ACCEPTED'
        """, nativeQuery = true)
    List<BookingDocument> findUserBookingValidDocuments(String cif, LocalDate bookingEndDate);
}
