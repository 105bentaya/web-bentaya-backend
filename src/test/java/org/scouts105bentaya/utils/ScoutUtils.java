package org.scouts105bentaya.utils;

import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.enums.ScoutType;

import java.util.ArrayList;

public class ScoutUtils {

    public static Scout basicScouter() {
        var scout = new Scout();
        scout.setId(1);
        scout.setGroup(GroupUtils.basicGroup());
        scout.setScoutType(ScoutType.SCOUTER);
        scout.setStatus(ScoutStatus.ACTIVE);
        return scout;
    }

    public static Scout basicScouter(Group group) {
        var scout = new Scout();
        scout.setId(1);
        scout.setGroup(group);
        scout.setScoutType(ScoutType.SCOUTER);
        scout.setStatus(ScoutStatus.ACTIVE);
        return scout;
    }

    public static Scout scoutOfId(int id) {
        var scout = new Scout();
        scout.setId(id);
        var medicalData = new MedicalData();
        medicalData.setDocuments(new ArrayList<>());
        scout.setMedicalData(medicalData);
        var economicData = new EconomicData();
        economicData.setDocuments(new ArrayList<>());
        scout.setEconomicData(economicData);
        var personalData = new PersonalData();
        personalData.setDocuments(new ArrayList<>());
        scout.setPersonalData(personalData);
        return scout;
    }
}
