package org.scouts105bentaya.features.event.converter;

import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventCalendarDto;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.Group;
import org.springframework.stereotype.Component;

@Component
public class EventCalendarConverter extends GenericConverter<Event, EventCalendarDto> {

    @Override
    public Event convertFromDto(EventCalendarDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public EventCalendarDto convertFromEntity(Event entity) {
        return new EventCalendarDto(
            entity.getId(),
            Group.valueFrom(entity.getGroupId()),
            entity.getTitle(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.isUnknownTime()
        );
    }
}
