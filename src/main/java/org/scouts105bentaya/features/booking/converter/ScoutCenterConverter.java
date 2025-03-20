package org.scouts105bentaya.features.booking.converter;

import org.scouts105bentaya.features.booking.dto.ScoutCenterDto;
import org.scouts105bentaya.features.booking.entity.ScoutCenter;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class ScoutCenterConverter extends GenericConverter<ScoutCenter, ScoutCenterDto> {

    @Override
    public ScoutCenter convertFromDto(ScoutCenterDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public ScoutCenterDto convertFromEntity(ScoutCenter entity) {
        return new ScoutCenterDto(
            entity.getId(),
            entity.getName(),
            entity.getPlace(),
            entity.getMaxCapacity(),
            entity.getMinExclusiveCapacity(),
            entity.getInformation(),
            entity.getFeatures(),
            entity.getPrice(),
            entity.getIcon()
        );
    }
}
