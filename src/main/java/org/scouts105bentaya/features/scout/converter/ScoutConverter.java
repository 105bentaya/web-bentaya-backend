package org.scouts105bentaya.features.scout.converter;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.group.GroupService;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScoutConverter extends GenericConverter<Scout, ScoutDto> {

    private final GroupService groupService;

    public ScoutConverter(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public Scout convertFromDto(ScoutDto dto) {
        Scout scout = new Scout();
//        scout.setId(dto.id());
//        scout.setName(dto.name());
//        scout.setSurname(dto.surname());
//        scout.setDni(dto.dni());
//        scout.setBirthday(dto.birthday());
//        scout.setGroup(groupService.findById(dto.group().id()));
//        scout.setMedicalData(dto.medicalData());
//        scout.setGender(dto.gender());
//        scout.setProgressions(dto.progressions());
//        scout.setObservations(dto.observations());
//        scout.setImageAuthorization(dto.imageAuthorization());
//        scout.setShirtSize(dto.shirtSize());
//        scout.setMunicipality(dto.municipality());
//        scout.setCensus(dto.census());
//        scout.setEnabled(dto.enabled());
//        scout.setContactList(dto.contactList().stream().map(contactConverter::convertFromDto).collect(Collectors.toList()));
        return scout;
    }

    @Override
    public ScoutDto convertFromEntity(Scout entity) {
        return new ScoutDto(
            entity.getId(),
            GroupBasicDataDto.fromGroup(entity.getGroup()),
            entity.getPersonalData().getName(),
            entity.getPersonalData().getSurname(),
            Optional.ofNullable(entity.getPersonalData().getIdDocument()).map(IdentificationDocument::getNumber).orElse(null),
            entity.getPersonalData().getBirthday(),
            entity.getMedicalData().getMedicalDiagnoses(),
            entity.getPersonalData().getGender(),
            entity.isImageAuthorization(),
            entity.getPersonalData().getShirtSize(),
            entity.getPersonalData().getResidenceMunicipality(),
            entity.getCensus(),
            entity.getProgressionsOld(),
            entity.getObservationsOld(),
            entity.getContactList(),
            entity.isActive(),
            entity.getUserList() != null && !entity.getUserList().isEmpty()
        );
    }
}
