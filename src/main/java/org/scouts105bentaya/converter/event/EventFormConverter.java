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
        EventFormDto dto = new EventFormDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setGroupId(Group.valueFrom(entity.getGroupId()));
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setLocalStartDate(entity.getStartDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate());
        dto.setLocalEndDate(entity.getEndDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate());
        dto.setLocation(entity.getLocation());
        dto.setLongitude(entity.getLongitude());
        dto.setLatitude(entity.getLatitude());
        dto.setActivateAttendanceList(entity.isActiveAttendanceList());
        dto.setActivateAttendancePayment(entity.isActiveAttendancePayment());
        dto.setCloseAttendanceList(entity.isClosedAttendanceList());
        dto.setUnknownTime(entity.isUnknownTime());
        return dto;
    }
}
