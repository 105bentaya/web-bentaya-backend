package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.MemberFile;
import org.scouts105bentaya.features.scout.entity.RealPersonalData;

import java.time.LocalDate;
import java.util.List;

public record RealPersonalDataDto(
    IdentificationDocument idDocument,
    String observations,
    List<MemberFile> documents,
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
) implements PersonalDataDto {
    public static RealPersonalDataDto from(RealPersonalData realPersonalData) {
        return new RealPersonalDataDto(
            realPersonalData.getIdDocument(),
            realPersonalData.getObservations(),
            realPersonalData.getDocuments(),
            realPersonalData.getSurname(),
            realPersonalData.getName(),
            realPersonalData.getFeltName(),
            realPersonalData.getBirthday(),
            realPersonalData.getBirthplace(),
            realPersonalData.getBirthProvince(),
            realPersonalData.getNationality(),
            realPersonalData.getAddress(),
            realPersonalData.getCity(),
            realPersonalData.getProvince(),
            realPersonalData.getPhone(),
            realPersonalData.getLandline(),
            realPersonalData.getEmail(),
            realPersonalData.getShirtSize(),
            realPersonalData.getResidenceMunicipality(),
            realPersonalData.getGender()
        );
    }
}
