package org.scouts105bentaya.converter.event;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.event.EventDto;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.enums.Group;
import org.springframework.stereotype.Component;

@Component
public class EventConverter extends GenericConverter<Event, EventDto> {

    @Override
    public Event convertFromDto(EventDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public EventDto convertFromEntity(Event entity) {
        return new EventDto(
            entity.getId(),
            Group.valueFrom(entity.getGroupId()),
            entity.getTitle(),
            entity.getDescription(),
            entity.getLocation(),
            entity.getLongitude(),
            entity.getLatitude(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.isUnknownTime(),
            entity.isActiveAttendanceList()
        );
    }
}
