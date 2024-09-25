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
        EventDto eventDto = new EventDto();
        eventDto.setId(entity.getId());
        eventDto.setTitle(entity.getTitle());
        eventDto.setGroupId(Group.valueFrom(entity.getGroupId()));
        eventDto.setDescription(entity.getDescription());
        eventDto.setStartDate(entity.getStartDate());
        eventDto.setEndDate(entity.getEndDate());
        eventDto.setLocation(entity.getLocation());
        eventDto.setLongitude(entity.getLongitude());
        eventDto.setLatitude(entity.getLatitude());
        eventDto.setHasAttendance(entity.isActiveAttendanceList());
        eventDto.setUnknownTime(entity.isUnknownTime());
        return eventDto;
    }
}
