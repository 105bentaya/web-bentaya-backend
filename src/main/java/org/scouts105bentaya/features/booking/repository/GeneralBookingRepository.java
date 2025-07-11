package org.scouts105bentaya.features.booking.repository;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GeneralBookingRepository extends JpaRepository<GeneralBooking, Integer> {
    default GeneralBooking get(Integer id) {
        return this.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    @Query("SELECT b FROM GeneralBooking b WHERE b.id IN (SELECT MAX(gb.id) FROM GeneralBooking gb WHERE gb.user.id = :userId GROUP BY gb.cif) ORDER BY b.id DESC")
    List<GeneralBooking> findLatestUserBookings(Integer userId);

    @Query("SELECT b FROM GeneralBooking b WHERE b.finished = false AND b.status = 'OCCUPIED' AND b.endDate < :date")
    List<GeneralBooking> findBookingsToBeFinished(LocalDateTime date);
}
