package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.entity.Scout;

public record UserScoutDto(
    Integer id,
    GroupBasicDataDto group,
    String name,
    String surname
) {
    public static UserScoutDto fromScout(Scout scout) {
        return new UserScoutDto(
            scout.getId(),
            GroupBasicDataDto.fromGroup(scout.getGroup()),
            scout.getPersonalData().getName(),
            scout.getPersonalData().getSurname()
        );
    }
}
