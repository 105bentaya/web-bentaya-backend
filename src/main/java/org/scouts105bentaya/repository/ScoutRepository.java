package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.enums.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoutRepository extends JpaRepository<Scout, Integer> {
    List<Scout> findAllByEnabledIsTrue();
    Optional<Scout> findByIdAndEnabledIsTrue(Integer id);
    List<Scout> findAllByGroupIdAndEnabledIsTrue(Group groupId);
    List<Scout> findAllByImageAuthorizationAndEnabledIsTrue(boolean imageAuthorization);
}
