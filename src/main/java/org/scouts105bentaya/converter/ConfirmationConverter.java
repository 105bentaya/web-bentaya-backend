package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.ConfirmationDto;
import org.scouts105bentaya.entity.Confirmation;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationConverter extends GenericConverter<Confirmation, ConfirmationDto> {

    @Override
    public Confirmation convertFromDto(ConfirmationDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public ConfirmationDto convertFromEntity(Confirmation entity) {
        return new ConfirmationDto(
            entity.getScout().getId(),
            entity.getEvent().getId(),
            entity.getAttending(),
            entity.getText(),
            entity.getPayed()
        );
    }
}
