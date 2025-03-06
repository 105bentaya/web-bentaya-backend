package org.scouts105bentaya.features.event.converter;

import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventFormConverter extends GenericConverter<Event, EventFormDto> {

    @Override
    public Event convertFromDto(EventFormDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public EventFormDto convertFromEntity(Event entity) {
        return new EventFormDto(
            entity.getId(),
            Optional.ofNullable(entity.getGroup()).map(Group::getId).orElse(null),
            entity.isForScouters(),
            entity.isForEveryone(),
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
            entity.isClosedAttendanceList(),
            entity.getCloseDateTime()
        );
    }
}
