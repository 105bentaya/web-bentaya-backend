package org.scouts105bentaya.features.scout;

import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoutRepository extends JpaRepository<Scout, Integer> {
    List<Scout> findAllByActiveIsTrue();

    Optional<Scout> findByIdAndActiveIsTrue(Integer id);

    List<Scout> findAllByGroupAndActiveIsTrue(Group group);

    List<Scout> findAllByImageAuthorizationAndActiveIsTrue(boolean imageAuthorization);

    Optional<Scout> findFirstByPersonalDataIdDocumentNumber(String idNumber);

    Optional<Scout> findFirstByCensus(Integer census);
}
