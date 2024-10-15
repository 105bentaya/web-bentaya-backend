package org.scouts105bentaya.features.scout.converter;

import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout_contact.ContactConverter;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.Group;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ScoutConverter extends GenericConverter<Scout, ScoutDto> {

    private final ContactConverter contactConverter;

    public ScoutConverter(ContactConverter contactConverter) {
        this.contactConverter = contactConverter;
    }

    @Override
    public Scout convertFromDto(ScoutDto dto) {
        Scout scout = new Scout();
        scout.setId(dto.id());
        scout.setName(dto.name());
        scout.setSurname(dto.surname());
        scout.setDni(dto.dni());
        scout.setBirthday(dto.birthday());
        scout.setGroupId(Group.valueOf(dto.groupId()));
        scout.setMedicalData(dto.medicalData());
        scout.setGender(dto.gender());
        scout.setProgressions(dto.progressions());
        scout.setObservations(dto.observations());
        scout.setImageAuthorization(dto.imageAuthorization());
        scout.setShirtSize(dto.shirtSize());
        scout.setMunicipality(dto.municipality());
        scout.setCensus(dto.census());
        scout.setEnabled(dto.enabled());
        scout.setContactList(dto.contactList().stream().map(contactConverter::convertFromDto).collect(Collectors.toList()));
        return scout;
    }

    @Override
    public ScoutDto convertFromEntity(Scout entity) {
        return new ScoutDto(
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
            entity.getProgressions(),
            entity.getObservations(),
            entity.getContactList().stream().map(contactConverter::convertFromEntity).toList(),
            entity.isEnabled(),
            entity.getUserList() != null && !entity.getUserList().isEmpty()
        );
    }
}
