package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.ScoutDto;
import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.enums.Group;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ScoutConverter extends GenericConverter<Scout, ScoutDto>{

    private final ContactConverter contactConverter;

    public ScoutConverter(ContactConverter contactConverter) {
        this.contactConverter = contactConverter;
    }

    @Override
    public Scout convertFromDto(ScoutDto dto) {
        Scout scout = new Scout();
        scout.setId(dto.getId());
        scout.setName(dto.getName());
        scout.setSurname(dto.getSurname());
        scout.setDni(dto.getDni());
        scout.setBirthday(dto.getBirthday());
        scout.setGroupId(Group.valueOf(dto.getGroupId()));
        scout.setMedicalData(dto.getMedicalData());
        scout.setGender(dto.getGender());
        scout.setProgressions(dto.getProgressions());
        scout.setObservations(dto.getObservations());
        scout.setImageAuthorization(dto.isImageAuthorization());
        scout.setShirtSize(dto.getShirtSize());
        scout.setMunicipality(dto.getMunicipality());
        scout.setCensus(dto.getCensus());
        scout.setEnabled(dto.isEnabled());
        scout.setContactList(dto.getContactList().stream().map(contactConverter::convertFromDto).collect(Collectors.toList()));
        return scout;
    }

    @Override
    public ScoutDto convertFromEntity(Scout entity) {
        ScoutDto scoutDto = new ScoutDto();
        scoutDto.setId(entity.getId());
        scoutDto.setName(entity.getName());
        scoutDto.setSurname(entity.getSurname());
        scoutDto.setGender(entity.getGender());
        scoutDto.setDni(entity.getDni());
        scoutDto.setBirthday(entity.getBirthday());
        scoutDto.setGroupId(Group.valueFrom(entity.getGroupId()));
        scoutDto.setMedicalData(entity.getMedicalData());
        scoutDto.setImageAuthorization(entity.isImageAuthorization());
        scoutDto.setProgressions(entity.getProgressions());
        scoutDto.setObservations(entity.getObservations());
        scoutDto.setShirtSize(entity.getShirtSize());
        scoutDto.setMunicipality(entity.getMunicipality());
        scoutDto.setCensus(entity.getCensus());
        scoutDto.setEnabled(entity.isEnabled());
        scoutDto.setUserAssigned(entity.getUserList() != null && !entity.getUserList().isEmpty());
        scoutDto.setContactList(entity.getContactList().stream().map(contactConverter::convertFromEntity).collect(Collectors.toList()));
        return scoutDto;
    }
}
