package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.JuridicalPersonalData;
import org.scouts105bentaya.features.scout.entity.JuridicalRepresentative;
import org.scouts105bentaya.features.scout.entity.MemberFile;

import java.util.List;

public record JuridicalPersonalDataDto(
    IdentificationDocument idDocument,
    String observations,
    List<MemberFile> documents,
    JuridicalRepresentative representative,
    String companyName
) implements PersonalDataDto {
    public static PersonalDataDto from(JuridicalPersonalData personalData) {
        return new JuridicalPersonalDataDto(
            personalData.getIdDocument(),
            personalData.getObservations(),
            personalData.getDocuments(),
            personalData.getRepresentative(),
            personalData.getCompanyName()
        );
    }
}
