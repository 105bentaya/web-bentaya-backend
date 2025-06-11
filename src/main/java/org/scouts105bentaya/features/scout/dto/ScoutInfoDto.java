package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.scouts105bentaya.features.scout.entity.ScoutRegistrationDates;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.enums.ScoutType;

import java.util.Comparator;
import java.util.List;

public record ScoutInfoDto(
    ScoutType scoutType,
    List<ScoutRegistrationDates> registrationDates,
    ScoutStatus status,
    boolean federated,
    Integer census,
    GroupBasicDataDto group,
    String section,
    List<ScoutRecord> recordList
) {
    public static ScoutInfoDto fromScout(Scout scout) {
        return new ScoutInfoDto(
            scout.getScoutType(),
            scout.getRegistrationDates().stream().sorted(Comparator.comparing(ScoutRegistrationDates::getRegistrationDate)).toList(),
            scout.getStatus(),
            scout.isFederated(),
            scout.getCensus(),
            GroupBasicDataDto.fromGroup(scout.getGroup()),
            ScoutUtils.getScoutSection(scout),
            scout.getRecordList()
        );
    }
}
