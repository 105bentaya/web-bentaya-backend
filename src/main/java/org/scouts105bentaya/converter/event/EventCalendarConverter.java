package org.scouts105bentaya.converter.event;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.event.EventCalendarDto;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.enums.Group;
import org.springframework.stereotype.Component;

@Component
public class EventCalendarConverter extends GenericConverter<Event, EventCalendarDto> {

    @Override
    public Event convertFromDto(EventCalendarDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public EventCalendarDto convertFromEntity(Event entity) {
        EventCalendarDto event = new EventCalendarDto();
        event.setEndDate(entity.getEndDate());
        event.setStartDate(entity.getStartDate());
        event.setGroupId(Group.valueFrom(entity.getGroupId()));
        event.setTitle(entity.getTitle());
        event.setId(entity.getId());
        event.setUnknownTime(entity.isUnknownTime());
        return event;
    }
}
