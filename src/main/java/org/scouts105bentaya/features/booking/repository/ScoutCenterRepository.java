package org.scouts105bentaya.features.booking.repository;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.ScoutCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoutCenterRepository extends JpaRepository<ScoutCenter, Integer> {
    default ScoutCenter get(int id) {
        return findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }
}
