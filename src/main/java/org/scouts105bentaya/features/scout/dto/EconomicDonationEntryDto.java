package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;

public record EconomicDonationEntryDto(
    int scoutId,
    Integer scoutCensus,
    String scoutName,
    String scoutSurname,
    String scoutFeltName,
    IdentificationDocument scoutIdDocument,
    EconomicEntry entry
) {
    public static EconomicDonationEntryDto fromEntry(EconomicEntry entry) {
        Scout scout = entry.getEconomicData().getScout();
        PersonalData personalData = entry.getEconomicData().getScout().getPersonalData();
        return new EconomicDonationEntryDto(
            scout.getId(),
            scout.getCensus(),
            personalData.getName(),
            personalData.getSurname(),
            personalData.getFeltName(),
            personalData.getIdDocument(),
            entry
        );
    }
}
