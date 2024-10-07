package org.scouts105bentaya.features.event;

import org.scouts105bentaya.shared.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByGroupId(Group groupId);

    List<Event> findAllByGroupIdAndActiveAttendanceListIsTrue(Group groupId);
}
