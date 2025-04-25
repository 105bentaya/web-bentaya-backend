package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.entity.MemberFile;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutRegistrationDates;
import org.scouts105bentaya.features.scout.enums.ScoutType;

import java.util.List;

public record ScoutInfoDto(
    ScoutType scoutType,
    List<ScoutRegistrationDates> registrationDates,
    boolean active,
    boolean federated,
    Integer census,
    boolean imageAuthorization,
    GroupBasicDataDto group,
    MemberFile photo
) {
    public static ScoutInfoDto fromScout(Scout scout) {
        return new ScoutInfoDto(
            scout.getScoutType(),
            scout.getRegistrationDates(),
            scout.isActive(),
            scout.isFederated(),
            scout.getCensus(),
            scout.isImageAuthorization(),
            GroupBasicDataDto.fromGroup(scout.getGroup()),
            scout.getPhoto()
        );
    }
}
