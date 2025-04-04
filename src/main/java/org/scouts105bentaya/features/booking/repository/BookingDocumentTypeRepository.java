package org.scouts105bentaya.features.booking.repository;


import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.BookingDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDocumentTypeRepository extends JpaRepository<BookingDocumentType, Integer> {
    default BookingDocumentType get(Integer id) {
        return findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    List<BookingDocumentType> findAllByActiveIsTrue();
}
