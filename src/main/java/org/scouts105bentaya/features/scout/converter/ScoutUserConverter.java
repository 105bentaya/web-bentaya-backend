package org.scouts105bentaya.features.scout.converter;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.scout.dto.ScoutUserDto;
import org.scouts105bentaya.features.scout_contact.ContactConverter;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ScoutUserConverter extends GenericConverter<Scout, ScoutUserDto> {

    private final ContactConverter contactConverter;

    public ScoutUserConverter(ContactConverter contactConverter) {
        this.contactConverter = contactConverter;
    }

    @Override
    public Scout convertFromDto(ScoutUserDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public ScoutUserDto convertFromEntity(Scout entity) {
        return new ScoutUserDto(
            entity.getId(),
            GroupBasicDataDto.fromGroup(entity.getGroup()),
            entity.getName(),
            entity.getSurname(),
            entity.getDni(),
            entity.getBirthday(),
            entity.getMedicalData(),
            entity.getGender(),
            entity.isImageAuthorization(),
            entity.getShirtSize(),
            entity.getMunicipality(),
            entity.getCensus(),
            entity.getContactList().stream().map(contactConverter::convertFromEntity).collect(Collectors.toList())
        );
    }
}
