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
    @Query("SELECT s FROM Scout s WHERE s.group = :group AND s.status <> 'INACTIVE'")
    List<Scout> findAllNotInactiveActiveByGroup(Group group); //todo check if works

    Optional<Scout> findFirstByPersonalDataIdDocumentNumber(String idNumber);

    Optional<Scout> findByCensus(int census);

    @Query("""
        SELECT s FROM Scout s
        LEFT JOIN s.personalData.idDocument d
           WHERE s.personalData.name LIKE :filter
              OR s.personalData.surname LIKE :filter
              OR CONCAT('', s.census) LIKE :filter
              OR d.number LIKE :filter
        """)
    List<Scout> findByBasicFields(String filter);
}
