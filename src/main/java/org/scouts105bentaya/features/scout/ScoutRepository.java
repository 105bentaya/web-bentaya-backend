package org.scouts105bentaya.features.scout;

import org.scouts105bentaya.features.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoutRepository extends JpaRepository<Scout, Integer> {
    List<Scout> findAllByEnabledIsTrue();

    Optional<Scout> findByIdAndEnabledIsTrue(Integer id);

    List<Scout> findAllByGroupAndEnabledIsTrue(Group group);

    List<Scout> findAllByImageAuthorizationAndEnabledIsTrue(boolean imageAuthorization);

    Optional<Scout> findFirstByDni(String dni);
}
