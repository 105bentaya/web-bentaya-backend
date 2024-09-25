package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.enums.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByGroupId(Group groupId);
    List<Event> findAllByGroupIdAndActiveAttendanceListIsTrue(Group groupId);
}
