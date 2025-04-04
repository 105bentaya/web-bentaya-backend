package org.scouts105bentaya.features.booking.repository;


import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Integer> {
    List<BookingDocument> findAllByBookingId(Integer bookingId);
}
