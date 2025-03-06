package org.scouts105bentaya.features.event;

import org.scouts105bentaya.features.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByGroup(Group group);
    List<Event> findAllByGroupAndActiveAttendanceListIsTrue(Group group);
}
