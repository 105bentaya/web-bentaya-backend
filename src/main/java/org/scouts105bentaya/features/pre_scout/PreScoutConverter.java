package org.scouts105bentaya.features.pre_scout;

import org.scouts105bentaya.features.pre_scout.dto.PreScoutAssignationDto;
import org.scouts105bentaya.features.pre_scout.dto.PreScoutDto;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class PreScoutConverter extends GenericConverter<PreScout, PreScoutDto> {

    @Override
    public PreScout convertFromDto(PreScoutDto preScoutDto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public PreScoutDto convertFromEntity(PreScout preScout) {
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
            PreScoutAssignationDto.ofPreScoutAssignation(preScout.getPreScoutAssignation()),
            preScout.getInscriptionYear(),
            preScout.getSize()
        );
    }
}
