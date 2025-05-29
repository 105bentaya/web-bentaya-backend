package org.scouts105bentaya.features.special_member.dto;

import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.special_member.entity.SpecialMember;
import org.scouts105bentaya.features.special_member.entity.SpecialMemberPerson;
import org.scouts105bentaya.features.scout.enums.PersonType;

import java.util.Optional;

public record SpecialMemberPersonDto(
    Integer scoutId,
    Integer personId,
    PersonType type,
    String name,
    String surname,
    String companyName,
    IdentificationDocument idDocument,
    String phone,
    String email
) {
    public static SpecialMemberPersonDto fromEntity(SpecialMember entity) {
        SpecialMemberPersonDto dto;
        if (Optional.ofNullable(entity.getScout()).isPresent()) {
            PersonalData personalData = entity.getScout().getPersonalData();
            dto = new SpecialMemberPersonDto(
                entity.getScout().getId(),
                null,
                PersonType.REAL,
                personalData.getName(),
                personalData.getSurname(),
                null,
                personalData.getIdDocument(),
                personalData.getPhone(),
                personalData.getEmail()
            );
        } else {
            SpecialMemberPerson person = entity.getPerson();
            dto = new SpecialMemberPersonDto(
                null,
                person.getId(),
                person.getType(),
                person.getName(),
                person.getSurname(),
                person.getCompanyName(),
                person.getIdDocument(),
                person.getPhone(),
                person.getEmail()
            );
        }
        return dto;
    }
}
