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
        preScout.setId(preScoutDto.getId());
        preScout.setName(preScoutDto.getName().toUpperCase());
        preScout.setSurname(preScoutDto.getSurname().toUpperCase());
        preScout.setSection(preScoutDto.getSection());
        preScout.setBirthday(preScoutDto.getBirthday());
        preScout.setAge(preScoutDto.getAge());
        preScout.setGender(preScoutDto.getGender().toUpperCase());
        preScout.setDni(preScoutDto.getDni().toUpperCase());
        preScout.setHasBeenInGroup(preScoutDto.isHasBeenInGroup());
        preScout.setYearAndSection(preScoutDto.getYearAndSection());
        preScout.setMedicalData(preScoutDto.getMedicalData());
        preScout.setParentsName(preScoutDto.getParentsName().toUpperCase());
        preScout.setRelationship(preScoutDto.getRelationship().toUpperCase());
        preScout.setPhone(preScoutDto.getPhone());
        preScout.setEmail(preScoutDto.getEmail().toLowerCase());
        preScout.setComment(preScoutDto.getComment());
        preScout.setPriority(preScoutDto.getPriority());
        preScout.setCreationDate(preScoutDto.getCreationDate());
        preScout.setParentsSurname(preScoutDto.getParentsSurname().toUpperCase());
        preScout.setPriorityInfo(preScoutDto.getPriorityInfo());
        preScout.setSize(preScoutDto.getSize().toUpperCase());
        preScout.setInscriptionYear(preScoutDto.getInscriptionYear());
        return preScout;
    }

    @Override
    public PreScoutDto convertFromEntity(PreScout preScout){
        PreScoutDto preScoutDto = new PreScoutDto();
        preScoutDto.setId(preScout.getId());
        preScoutDto.setName(preScout.getName());
        preScoutDto.setSurname(preScout.getSurname());
        preScoutDto.setSection(preScout.getSection());
        preScoutDto.setBirthday(preScout.getBirthday());
        preScoutDto.setAge(preScout.getAge());
        preScoutDto.setGender(preScout.getGender());
        preScoutDto.setDni(preScout.getDni());
        preScoutDto.setHasBeenInGroup(preScout.isHasBeenInGroup());
        preScoutDto.setYearAndSection(preScout.getYearAndSection());
        preScoutDto.setMedicalData(preScout.getMedicalData());
        preScoutDto.setParentsName(preScout.getParentsName());
        preScoutDto.setRelationship(preScout.getRelationship());
        preScoutDto.setPhone(preScout.getPhone());
        preScoutDto.setEmail(preScout.getEmail());
        preScoutDto.setComment(preScout.getComment());
        preScoutDto.setPriority(preScout.getPriority());
        preScoutDto.setCreationDate(preScout.getCreationDate());
        preScoutDto.setStatus(Optional.ofNullable(preScout.getPreScoutAssignation()).map(PreScoutAssignation::getStatus).orElse(null));
        preScoutDto.setAssignationComment(Optional.ofNullable(preScout.getPreScoutAssignation()).map(PreScoutAssignation::getComment).orElse(null));
        preScoutDto.setAssignationDate(Optional.ofNullable(preScout.getPreScoutAssignation()).map(PreScoutAssignation::getAssignationDate).orElse(null));
        preScoutDto.setGroupId(Optional.ofNullable(preScout.getPreScoutAssignation()).map(preScoutAssignation -> preScoutAssignation.getGroupId().getValue()).orElse(null));
        preScoutDto.setParentsSurname(preScout.getParentsSurname());
        preScoutDto.setPriorityInfo(preScout.getPriorityInfo());
        preScoutDto.setSize(preScout.getSize());
        preScoutDto.setInscriptionYear(preScout.getInscriptionYear());
        return preScoutDto;
    }
}
