package org.scouts105bentaya.features.event;

import org.scouts105bentaya.features.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByGroup(Group group);
    List<Event> findAllByGroupAndActiveAttendanceListIsTrue(Group group);

    @Query("SELECT e FROM Event e where e.unknownTime = false AND (e.endDate = :endDate or e.startDate = :startDate or e.startDate = :endDate or e.endDate = :startDate)")
    List<Event> findEventsWithStartOrEndCoincidence(ZonedDateTime endDate, ZonedDateTime startDate);
}
