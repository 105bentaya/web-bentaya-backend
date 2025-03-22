package org.scouts105bentaya.features.scout_center.repository;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenterFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoutCenterFileRepository extends JpaRepository<ScoutCenterFile, Integer> {
    default ScoutCenterFile get(int id) {
        return findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    Optional<ScoutCenterFile> findByUuid(String uuid);
}
