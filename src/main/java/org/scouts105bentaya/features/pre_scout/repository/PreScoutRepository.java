package org.scouts105bentaya.features.pre_scout.repository;

import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.shared.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreScoutRepository extends JpaRepository<PreScout, Integer> {
    List<PreScout> findAllByPreScoutAssignation_GroupId(Group groupId);
}
