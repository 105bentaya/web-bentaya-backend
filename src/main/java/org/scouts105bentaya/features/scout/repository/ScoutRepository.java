package org.scouts105bentaya.features.scout.repository;

import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoutRepository extends JpaRepository<Scout, Integer>, JpaSpecificationExecutor<Scout> {
    List<Scout> findAllByActiveIsTrue();

    Optional<Scout> findByIdAndActiveIsTrue(Integer id);

    List<Scout> findAllByGroupAndActiveIsTrue(Group group);

    List<Scout> findAllByPersonalDataImageAuthorizationAndActiveIsTrue(boolean imageAuthorization);

    Optional<Scout> findFirstByPersonalDataIdDocumentNumber(String idNumber);

    Optional<Scout> findFirstByCensus(Integer census);

    @Query("SELECT s FROM Scout s WHERE s.personalData.name LIKE :filter OR s.personalData.surname LIKE :filter or CONCAT('', s.census) LIKE :filter or s.personalData.idDocument.number LIKE :filter")
    List<Scout> findByBasicFields(String filter);
}
