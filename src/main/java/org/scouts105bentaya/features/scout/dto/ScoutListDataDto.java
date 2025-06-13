package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.ScoutType;

import java.time.LocalDate;

public record ScoutListDataDto(
    int id,
    String name,
    String surname,
    String feltName,
    ScoutType scoutType,
    GroupBasicDataDto group,
    LocalDate birthday,
    String gender,
    IdentificationDocument idDocument,
    Integer census,
    String email
) {
    public static ScoutListDataDto fromScout(Scout scout) {
        return new ScoutListDataDto(
            scout.getId(),
            scout.getPersonalData().getName(),
            scout.getPersonalData().getSurname(),
            scout.getPersonalData().getFeltName(),
            scout.getScoutType(),
            GroupBasicDataDto.fromGroup(scout.getGroup()),
            scout.getPersonalData().getBirthday(),
            scout.getPersonalData().getGender(),
            scout.getPersonalData().getIdDocument(),
            scout.getCensus(),
            scout.getPersonalData().getEmail()
        );
    }
}
