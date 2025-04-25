package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.Member;
import org.scouts105bentaya.features.scout.entity.MemberFile;
import org.scouts105bentaya.features.scout.entity.MemberRoleInfo;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.PersonType;

import java.util.List;
import java.util.Set;

public record MemberDto(
    int id,
    PersonType type,
    Set<MemberRoleInfo> roleInfos,
    String observations,
    List<MemberFile> extraFiles,
    List<MemberFile> images,
    PersonalDataDto personalData,
    ScoutInfoDto scoutInfo
) {
    public static MemberDto fromScout(Scout scout) {
        return new MemberDto(
            scout.getId(),
            scout.getType(),
            scout.getRoles(),
            scout.getObservations(),
            scout.getExtraFiles(),
            scout.getImages(),
            RealPersonalDataDto.from(scout.getPersonalData()),
            ScoutInfoDto.fromScout(scout)
        );
    }

    public static MemberDto fromMember(Member member) {
        return new MemberDto(
            member.getId(),
            member.getType(),
            member.getRoles(),
            member.getObservations(),
            member.getExtraFiles(),
            member.getImages(),
            PersonalDataDto.fromPersonalData(member.getPersonalData()),
            (member instanceof Scout scout) ? ScoutInfoDto.fromScout(scout) : null
        );
    }
}
