package org.scouts105bentaya.features.scout;

import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoutRecordRepository extends JpaRepository<ScoutRecord, Integer> {
}
