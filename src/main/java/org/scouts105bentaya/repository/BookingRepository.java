package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.Booking;
import org.scouts105bentaya.enums.ScoutCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findBookingByScoutCenterAndEndDateIsAfter(ScoutCenter scoutCenter, LocalDateTime endDate);

    List<Booking> findBookingByScoutCenterAndEndDateIsAfterAndStartDateIsBefore(ScoutCenter scoutCenter, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findBookingByScoutCenter(ScoutCenter scoutCenter);

    List<Booking> findBookingByUserId(Integer user_id);

    Optional<Booking> findFirstByUserIdOrderByCreationDateDesc(Integer user_id);
}
