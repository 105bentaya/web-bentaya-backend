package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.attendance.AttendanceInfoDto;
import org.scouts105bentaya.entity.Confirmation;
import org.springframework.stereotype.Component;

@Component
public class AttendanceInfoConverter extends GenericConverter<Confirmation, AttendanceInfoDto> {
    @Override
    public Confirmation convertFromDto(AttendanceInfoDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public AttendanceInfoDto convertFromEntity(Confirmation entity) {
        return new AttendanceInfoDto(
            entity.getScout().getName(),
            entity.getScout().getSurname(),
            entity.getScout().getId(),
            entity.getAttending(),
            entity.getPayed(),
            entity.getText(),
            entity.getScout().getMedicalData()
        );
    }
}