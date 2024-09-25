package org.scouts105bentaya.repository;


import org.scouts105bentaya.entity.BookingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Integer> {
    List<BookingDocument> findByBookingId(Integer bookingId);
}
