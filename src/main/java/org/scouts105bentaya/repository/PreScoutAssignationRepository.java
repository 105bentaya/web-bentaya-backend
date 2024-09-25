package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.PreScoutAssignation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreScoutAssignationRepository extends JpaRepository<PreScoutAssignation, Integer> {
}
