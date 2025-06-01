package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.Contact;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.entity.ScoutHistory;
import org.scouts105bentaya.features.special_member.entity.SpecialMember;

import java.util.List;

public record ScoutDto(
    int id,
    List<SpecialMember> roleInfos,
    String observations,
    List<ScoutFile> extraFiles,
    List<ScoutFile> images,
    PersonalData personalData,
    List<Contact> contactList,
    MedicalData medicalData,
    EconomicData economicData,
    ScoutHistory scoutHistory,
    ScoutInfoDto scoutInfo
) {
    public static ScoutDto fromScout(Scout scout) {
        return new ScoutDto(
            scout.getId(),
            scout.getSpecialRoles(),
            scout.getObservations(),
            scout.getExtraFiles(),
            scout.getImages(),
            scout.getPersonalData(),
            scout.getContactList(),
            scout.getMedicalData(),
            scout.getEconomicData(),
            scout.getScoutHistory(),
            ScoutInfoDto.fromScout(scout)
        );
    }
}
