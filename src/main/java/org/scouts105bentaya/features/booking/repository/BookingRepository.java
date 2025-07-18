package org.scouts105bentaya.features.booking.repository;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    default Booking get(Integer bookingId) {
        return findById(bookingId).orElseThrow(WebBentayaNotFoundException::new);
    }

    List<Booking> findBookingByScoutCenterIdAndEndDateIsAfter(Integer scoutCenterId, LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.scoutCenter.id = :centerId AND b.startDate < :endDate and b.endDate > :startDate")
    List<Booking> findAllOverlapping(LocalDateTime startDate, LocalDateTime endDate, Integer centerId);
}
