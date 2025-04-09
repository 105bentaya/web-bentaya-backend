package org.scouts105bentaya.features.booking.repository;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnBookingRepository extends JpaRepository<OwnBooking, Integer>, JpaSpecificationExecutor<OwnBooking> {
    default OwnBooking get(Integer id) {
        return this.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }
}
