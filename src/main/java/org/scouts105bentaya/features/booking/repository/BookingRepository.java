package org.scouts105bentaya.features.booking.repository;

import org.scouts105bentaya.features.booking.ScoutCenter;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingByScoutCenterAndEndDateIsAfter(ScoutCenter scoutCenter, LocalDateTime endDate);
    List<Booking> findBookingByScoutCenterAndEndDateIsAfterAndStartDateIsBefore(ScoutCenter scoutCenter, LocalDateTime startDate, LocalDateTime endDate);
    List<Booking> findBookingByScoutCenter(ScoutCenter scoutCenter);
    List<Booking> findBookingByUserId(Integer userId);
    Optional<Booking> findFirstByUserIdOrderByCreationDateDesc(Integer userId);
}
