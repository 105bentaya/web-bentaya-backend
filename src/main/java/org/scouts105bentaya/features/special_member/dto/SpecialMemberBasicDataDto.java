package org.scouts105bentaya.features.special_member.dto;

import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.features.special_member.entity.SpecialMember;
import org.scouts105bentaya.features.special_member.entity.SpecialMemberPerson;
import org.scouts105bentaya.features.special_member.enums.SpecialMemberRole;

import java.util.Optional;

public record SpecialMemberBasicDataDto(
    int id,
    SpecialMemberRole role,
    int roleCensus,
    String name
) {
    public static SpecialMemberBasicDataDto fromEntity(SpecialMember entity) {
        String name;
        if (Optional.ofNullable(entity.getScout()).isPresent()) {
            PersonalData personalData = entity.getScout().getPersonalData();
            name = personalData.getName() + " " + personalData.getSurname();
        } else {
            SpecialMemberPerson person = entity.getPerson();
            name = person.getType() == PersonType.REAL ?
                person.getName() + " " + person.getSurname() :
                person.getCompanyName();
        }
        return new SpecialMemberBasicDataDto(
            entity.getId(),
            entity.getRole(),
            entity.getRoleCensus(),
            name.trim()
        );
    }
}
