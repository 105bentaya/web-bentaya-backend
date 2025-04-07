package org.scouts105bentaya.features.booking.repository;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralBookingRepository extends JpaRepository<GeneralBooking, Integer> {
    default GeneralBooking get(Integer id) {
        return this.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    Optional<GeneralBooking> findFirstByUserIdOrderByCreationDateDesc(Integer userId);

    @Query("SELECT b FROM GeneralBooking b WHERE b.finished = false AND b.status = 'OCCUPIED' AND b.endDate < :date")
    List<GeneralBooking> findBookingsToBeFinished(LocalDateTime date);
}
