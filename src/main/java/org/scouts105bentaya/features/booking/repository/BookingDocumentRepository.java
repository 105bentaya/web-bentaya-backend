package org.scouts105bentaya.features.booking.repository;


import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Integer> {
    List<BookingDocument> findAllByBookingId(Integer bookingId);

    @Query("""
        SELECT b FROM BookingDocument b
        WHERE b.id IN (
            SELECT MAX(bd.id) FROM BookingDocument bd
            WHERE bd.booking.user.id = :userId
              AND bd.status = 'ACCEPTED'
              AND (
                bd.duration = 'PERMANENT'
                OR (bd.duration = 'EXPIRABLE' AND bd.expirationDate > :bookingEndDate)
              )
            GROUP BY bd.file.id
        )
        """)
    List<BookingDocument> findUserBookingValidDocuments(Integer userId, LocalDate bookingEndDate);
}
