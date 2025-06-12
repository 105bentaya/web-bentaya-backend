package org.scouts105bentaya.features.scout.repository;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
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

    default Scout get(Integer id) {
        return findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    @Query("SELECT s FROM Scout s WHERE s.scoutType = 'SCOUT' AND s.group = :group")
    List<Scout> findScoutsByGroup(Group group);

    Optional<Scout> findFirstByPersonalDataIdDocumentNumber(String idNumber);

    Optional<Scout> findByPersonalDataEmail(String email);

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

    @Query(value = """
        SELECT u.username
        FROM user u
        WHERE u.id IN (SELECT us.user_id FROM user_scouts us WHERE us.scout_id = :scoutId)
            OR u.id IN (SELECT us.user_id FROM user_scouter us WHERE us.scout_id = :scoutId)
        """, nativeQuery = true)
    List<String> findScoutsUserNames(Integer scoutId);
}
