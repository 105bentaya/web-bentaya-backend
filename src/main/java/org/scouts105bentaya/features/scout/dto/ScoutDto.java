package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.entity.SpecialMember;

import java.util.List;
import java.util.Set;

public record ScoutDto(
    int id,
    Set<SpecialMember> roleInfos,
    String observations,
    List<ScoutFile> extraFiles,
    List<ScoutFile> images,
    PersonalDataDto personalData,
    ScoutInfoDto scoutInfo
) {
    public static ScoutDto fromScout(Scout scout) {
        return new ScoutDto(
            scout.getId(),
            scout.getSpecialRoles(),
            scout.getObservations(),
            scout.getExtraFiles(),
            scout.getImages(),
            PersonalDataDto.from(scout.getPersonalData()),
            ScoutInfoDto.fromScout(scout)
        );
    }
}
