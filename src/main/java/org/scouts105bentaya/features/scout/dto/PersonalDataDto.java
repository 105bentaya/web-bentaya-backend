package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.ScoutFile;

import java.time.LocalDate;
import java.util.List;

public record PersonalDataDto(
    IdentificationDocument idDocument,
    String observations,
    List<ScoutFile> documents,
    String surname,
    String name,
    String feltName,
    LocalDate birthday,
    String birthplace,
    String birthProvince,
    String nationality,
    String address,
    String city,
    String province,
    String phone,
    String landline,
    String email,
    String shirtSize,
    String residenceMunicipality,
    String gender
) {
    public static PersonalDataDto from(PersonalData personalData) {
        return new PersonalDataDto(
            personalData.getIdDocument(),
            personalData.getObservations(),
            personalData.getDocuments(),
            personalData.getSurname(),
            personalData.getName(),
            personalData.getFeltName(),
            personalData.getBirthday(),
            personalData.getBirthplace(),
            personalData.getBirthProvince(),
            personalData.getNationality(),
            personalData.getAddress(),
            personalData.getCity(),
            personalData.getProvince(),
            personalData.getPhone(),
            personalData.getLandline(),
            personalData.getEmail(),
            personalData.getShirtSize(),
            personalData.getResidenceMunicipality(),
            personalData.getGender()
        );
    }
}
