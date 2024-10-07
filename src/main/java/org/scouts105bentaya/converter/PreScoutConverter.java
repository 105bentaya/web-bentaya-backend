package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.PreScoutDto;
import org.scouts105bentaya.entity.PreScout;
import org.scouts105bentaya.entity.PreScoutAssignation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PreScoutConverter extends GenericConverter<PreScout, PreScoutDto> {

    @Override
    public PreScout convertFromDto(PreScoutDto preScoutDto){
        PreScout preScout = new PreScout();
        preScout.setId(preScoutDto.id());
        preScout.setName(preScoutDto.name().toUpperCase());
        preScout.setSurname(preScoutDto.surname().toUpperCase());
        preScout.setSection(preScoutDto.section());
        preScout.setBirthday(preScoutDto.birthday());
        preScout.setAge(preScoutDto.age());
        preScout.setGender(preScoutDto.gender().toUpperCase());
        preScout.setDni(preScoutDto.dni().toUpperCase());
        preScout.setHasBeenInGroup(preScoutDto.hasBeenInGroup());
        preScout.setYearAndSection(preScoutDto.yearAndSection());
        preScout.setMedicalData(preScoutDto.medicalData());
        preScout.setParentsName(preScoutDto.parentsName().toUpperCase());
        preScout.setRelationship(preScoutDto.relationship().toUpperCase());
        preScout.setPhone(preScoutDto.phone());
        preScout.setEmail(preScoutDto.email().toLowerCase());
        preScout.setComment(preScoutDto.comment());
        preScout.setPriority(preScoutDto.priority());
        preScout.setCreationDate(preScoutDto.creationDate());
        preScout.setParentsSurname(preScoutDto.parentsSurname().toUpperCase());
        preScout.setPriorityInfo(preScoutDto.priorityInfo());
        preScout.setSize(preScoutDto.size().toUpperCase());
        preScout.setInscriptionYear(preScoutDto.inscriptionYear());
        return preScout;
    }

    @Override
    public PreScoutDto convertFromEntity(PreScout preScout){
        return new PreScoutDto(
            preScout.getId(),
            preScout.getName(),
            preScout.getSurname(),
            preScout.getSection(),
            preScout.getBirthday(),
            preScout.getAge(),
            preScout.getGender(),
            preScout.getDni(),
            preScout.isHasBeenInGroup(),
            preScout.getYearAndSection(),
            preScout.getMedicalData(),
            preScout.getParentsName(),
            preScout.getParentsSurname(),
            preScout.getRelationship(),
            preScout.getPhone(),
            preScout.getEmail(),
            preScout.getComment(),
            preScout.getPriority(),
            preScout.getPriorityInfo(),
            preScout.getCreationDate(),
            Optional.ofNullable(preScout.getPreScoutAssignation()).map(PreScoutAssignation::getStatus).orElse(null),
            Optional.ofNullable(preScout.getPreScoutAssignation()).map(preScoutAssignation -> preScoutAssignation.getGroupId().getValue()).orElse(null),
            Optional.ofNullable(preScout.getPreScoutAssignation()).map(PreScoutAssignation::getComment).orElse(null),
            Optional.ofNullable(preScout.getPreScoutAssignation()).map(PreScoutAssignation::getAssignationDate).orElse(null),
            preScout.getInscriptionYear(),
            preScout.getSize()
        );
    }
}
