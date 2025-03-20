package org.scouts105bentaya.features.booking.converter;

import org.scouts105bentaya.features.booking.dto.ScoutCenterWithFilesDto;
import org.scouts105bentaya.features.booking.entity.ScoutCenter;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class ScoutCenterWithFilesConverter extends GenericConverter<ScoutCenter, ScoutCenterWithFilesDto> {

    private final ScoutCenterConverter scoutCenterConverter;

    public ScoutCenterWithFilesConverter(ScoutCenterConverter scoutCenterConverter) {
        super();
        this.scoutCenterConverter = scoutCenterConverter;
    }

    @Override
    public ScoutCenter convertFromDto(ScoutCenterWithFilesDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public ScoutCenterWithFilesDto convertFromEntity(ScoutCenter entity) {
        return new ScoutCenterWithFilesDto(
            scoutCenterConverter.convertFromEntity(entity),
            entity.getRulePdf(),
            entity.getIncidencesDoc(),
            entity.getAttendanceDoc(),
            entity.getMainPhoto(),
            entity.getPhotos()
        );
    }
}
