package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.entity.Contact;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.Scout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record UserScoutDto(
    Integer id,
    GroupBasicDataDto group,
    String name,
    String surname,
    String dni,
    LocalDate birthday,
    String medicalData,
    String gender,
    boolean imageAuthorization,
    String shirtSize,
    String municipality,
    Integer census,
    List<Contact> contactList
) {
    public static UserScoutDto fromScout(Scout scout) {
        return new UserScoutDto(
            scout.getId(),
            GroupBasicDataDto.fromGroup(scout.getGroup()),
            scout.getPersonalData().getName(),
            scout.getPersonalData().getSurname(),
            Optional.ofNullable(scout.getPersonalData().getIdDocument()).map(IdentificationDocument::getNumber).orElse(null),
            scout.getPersonalData().getBirthday(),
            scout.getMedicalData().getMedicalDiagnoses(),
            scout.getPersonalData().getGender(),
            scout.getPersonalData().isImageAuthorization(),
            scout.getPersonalData().getShirtSize(),
            scout.getPersonalData().getResidenceMunicipality(),
            scout.getCensus(),
            scout.getContactList()
        );
    }
}
