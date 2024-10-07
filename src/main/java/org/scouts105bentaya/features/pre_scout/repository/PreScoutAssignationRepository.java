package org.scouts105bentaya.features.pre_scout.repository;

import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreScoutAssignationRepository extends JpaRepository<PreScoutAssignation, Integer> {
}
