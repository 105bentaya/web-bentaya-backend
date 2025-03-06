package org.scouts105bentaya.features.event.converter;

import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventCalendarDto;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventCalendarConverter extends GenericConverter<Event, EventCalendarDto> {

    @Override
    public Event convertFromDto(EventCalendarDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public EventCalendarDto convertFromEntity(Event entity) {
        return new EventCalendarDto(
            entity.getId(),
            Optional.ofNullable(entity.getGroup()).map(Group::getId).orElse(null),
            entity.getTitle(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.isUnknownTime(),
            entity.isForEveryone(),
            entity.isForScouters()
        );
    }
}
