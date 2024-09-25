package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.PreScout;
import org.scouts105bentaya.enums.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreScoutRepository extends JpaRepository<PreScout, Integer> {
    List<PreScout> findAllByPreScoutAssignation_GroupId(Group groupId);
}
