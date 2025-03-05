package org.scouts105bentaya.features.confirmation.converter;

import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.dto.ConfirmationDto;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationConverter extends GenericConverter<Confirmation, ConfirmationDto> {

    @Override
    public Confirmation convertFromDto(ConfirmationDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
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
