package org.scouts105bentaya.features.confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation, ConfirmationId> {
    List<Confirmation> findAllByEventId(Integer eventId);

    List<Confirmation> findAllByScoutId(Integer scoutId);

    @Query("select c from Confirmation c where c.event.id = :eventId AND c.scout.id in :scoutIds")
    List<Confirmation> findAllByEventIdInScoutIds(@Param("eventId") Integer eventId, @Param("scoutIds") List<Integer> scoutIds);
}