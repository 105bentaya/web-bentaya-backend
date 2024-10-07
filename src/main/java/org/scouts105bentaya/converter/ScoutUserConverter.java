package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.ScoutUserDto;
import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.enums.Group;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ScoutUserConverter extends GenericConverter<Scout, ScoutUserDto>{

    private final ContactConverter contactConverter;

    public ScoutUserConverter(ContactConverter contactConverter) {
        this.contactConverter = contactConverter;
    }

    @Override
    public Scout convertFromDto(ScoutUserDto dto) {
        Scout scout = new Scout();
        scout.setId(dto.id());
        scout.setName(dto.name());
        scout.setSurname(dto.surname());
        scout.setDni(dto.dni());
        scout.setBirthday(dto.birthday());
        scout.setGroupId(Group.valueOf(dto.groupId()));
        scout.setMedicalData(dto.medicalData());
        scout.setGender(dto.gender());
        scout.setImageAuthorization(dto.imageAuthorization());
        scout.setShirtSize(dto.shirtSize());
        scout.setMunicipality(dto.municipality());
        scout.setCensus(dto.census());
        scout.setContactList(dto.contactList().stream().map(contactConverter::convertFromDto).collect(Collectors.toList()));
        return scout;
    }

    @Override
    public ScoutUserDto convertFromEntity(Scout entity) {
        return new ScoutUserDto(
            entity.getId(),
            Group.valueFrom(entity.getGroupId()),
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
