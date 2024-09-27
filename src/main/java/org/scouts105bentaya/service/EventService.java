package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.event.EventFormDto;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.enums.Group;

import java.util.List;

public interface EventService {
    List<Event> findAll();
    List<Event> findAllByGroupId(Group id);
    List<Event> findAllByGroupIdAndActivatedAttendance(Group id);
    Event findById(Integer id);
    Event save(EventFormDto eventDto);
    Event update(EventFormDto eventDto);
    void delete(Integer id);
}
