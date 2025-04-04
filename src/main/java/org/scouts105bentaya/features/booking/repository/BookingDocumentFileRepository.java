package org.scouts105bentaya.features.booking.repository;


import org.scouts105bentaya.features.booking.entity.BookingDocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDocumentFileRepository extends JpaRepository<BookingDocumentFile, Integer> {
}
