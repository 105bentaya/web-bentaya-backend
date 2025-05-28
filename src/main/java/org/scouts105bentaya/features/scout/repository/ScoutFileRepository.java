package org.scouts105bentaya.features.scout.repository;

import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoutFileRepository extends JpaRepository<ScoutFile, Integer> {
}
