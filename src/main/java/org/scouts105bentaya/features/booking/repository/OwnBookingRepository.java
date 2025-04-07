package org.scouts105bentaya.features.booking.repository;

import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnBookingRepository extends JpaRepository<OwnBooking, Integer> {
}
