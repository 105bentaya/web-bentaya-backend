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
        AttendanceInfoDto dto = new AttendanceInfoDto();
        dto.setName(entity.getScout().getName());
        dto.setSurname(entity.getScout().getSurname());
        dto.setMedicalData(entity.getScout().getMedicalData());
        dto.setText(entity.getText());
        dto.setAttending(entity.getAttending());
        dto.setScoutId(entity.getScout().getId());
        dto.setPayed(entity.getPayed());
        return dto;
    }
}