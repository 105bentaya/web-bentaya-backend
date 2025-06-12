package org.scouts105bentaya.features.scout;

import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutInfoDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class ScoutConverter extends GenericConverter<Scout, ScoutDto> {
    private final ScoutRepository scoutRepository;

    public ScoutConverter(ScoutRepository scoutRepository) {
        super();
        this.scoutRepository = scoutRepository;
    }

    @Override
    public Scout convertFromDto(ScoutDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public ScoutDto convertFromEntity(Scout scout) {
        return new ScoutDto(
            scout.getId(),
            scout.getSpecialRoles(),
            scout.getPersonalData(),
            scout.getContactList(),
            scout.getMedicalData(),
            scout.getEconomicData(),
            scout.getScoutHistory(),
            ScoutInfoDto.fromScout(scout),
            scoutRepository.findScoutsUserNames(scout.getId())
        );
    }
}
