package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.Contact;
import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.ScoutHistory;
import org.scouts105bentaya.features.special_member.entity.SpecialMember;

import java.util.List;

public record ScoutDto(
    int id,
    List<SpecialMember> roleInfos,
    PersonalData personalData,
    List<Contact> contactList,
    MedicalData medicalData,
    EconomicData economicData,
    ScoutHistory scoutHistory,
    ScoutInfoDto scoutInfo,
    List<String> usernames
) {
}
