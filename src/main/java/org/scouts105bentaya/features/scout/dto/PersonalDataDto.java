package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.JuridicalPersonalData;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.RealPersonalData;

public interface PersonalDataDto {
    static PersonalDataDto fromPersonalData(PersonalData personalData) {
        if (personalData instanceof RealPersonalData realPersonalData) {
            return RealPersonalDataDto.from(realPersonalData);
        } else {
            return JuridicalPersonalDataDto.from((JuridicalPersonalData) personalData);
        }
    }
}
