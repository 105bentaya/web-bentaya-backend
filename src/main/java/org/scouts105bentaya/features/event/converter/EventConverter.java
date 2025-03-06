package org.scouts105bentaya.features.event.converter;

import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventDto;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class EventConverter extends GenericConverter<Event, EventDto> {

    @Override
    public Event convertFromDto(EventDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public EventDto convertFromEntity(Event entity) {
        return new EventDto(
            entity.getId(),
            GroupBasicDataDto.fromGroup(entity.getGroup()),
            entity.isForScouters(),
            entity.isForEveryone(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getLocation(),
            entity.getLongitude(),
            entity.getLatitude(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.isUnknownTime(),
            entity.isActiveAttendanceList(),
            entity.eventAttendanceIsClosed(),
            entity.getCloseDateTime()
        );
    }
}
