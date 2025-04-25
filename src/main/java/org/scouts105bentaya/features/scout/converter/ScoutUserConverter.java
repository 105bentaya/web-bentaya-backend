package org.scouts105bentaya.features.scout.converter;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.dto.ScoutUserDto;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScoutUserConverter extends GenericConverter<Scout, ScoutUserDto> {

    @Override
    public Scout convertFromDto(ScoutUserDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public ScoutUserDto convertFromEntity(Scout entity) {
        return new ScoutUserDto(
            entity.getId(),
            GroupBasicDataDto.fromGroup(entity.getGroup()),
            entity.getPersonalData().getName(),
            entity.getPersonalData().getSurname(),
            Optional.ofNullable(entity.getPersonalData().getIdDocument()).map(IdentificationDocument::getNumber).orElse(null),
            entity.getPersonalData().getBirthday(),
            entity.getMedicalDataOld(),
            entity.getPersonalData().getGender(),
            entity.isImageAuthorization(),
            entity.getPersonalData().getShirtSize(),
            entity.getPersonalData().getResidenceMunicipality(),
            entity.getCensus(),
            entity.getContactList()
        );
    }
}
