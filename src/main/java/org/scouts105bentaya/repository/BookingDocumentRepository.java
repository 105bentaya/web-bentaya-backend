package org.scouts105bentaya.repository;


import org.scouts105bentaya.entity.BookingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Integer> {
    List<BookingDocument> findByBookingId(Integer bookingId);
}
