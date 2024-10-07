package org.scouts105bentaya.converter.event;

import org.scouts105bentaya.constant.GenericConstants;
import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.event.EventFormDto;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.enums.Group;
import org.springframework.stereotype.Component;

@Component
public class EventFormConverter extends GenericConverter<Event, EventFormDto> {

    @Override
    public Event convertFromDto(EventFormDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public EventFormDto convertFromEntity(Event entity) {
        return new EventFormDto(
            entity.getId(),
            Group.valueFrom(entity.getGroupId()),
            entity.getTitle(),
            entity.getDescription(),
            entity.getLocation(),
            entity.getLongitude(),
            entity.getLatitude(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getStartDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate(),
            entity.getEndDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate(),
            entity.isUnknownTime(),
            entity.isActiveAttendanceList(),
            entity.isActiveAttendancePayment(),
            entity.isClosedAttendanceList()
        );
    }
}
