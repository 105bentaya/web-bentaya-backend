package org.scouts105bentaya.features.special_member.dto;

import org.scouts105bentaya.features.special_member.entity.SpecialMember;
import org.scouts105bentaya.features.special_member.SpecialMemberRole;

import java.time.LocalDate;

public record SpecialMemberDetailRecordDto(
    int id,
    SpecialMemberRole role,
    int roleCensus,
    LocalDate awardDate,
    LocalDate agreementDate,
    String details,
    String observations
) {
    public static SpecialMemberDetailRecordDto fromEntity(SpecialMember entity) {
        return new SpecialMemberDetailRecordDto(
            entity.getId(),
            entity.getRole(),
            entity.getRoleCensus(),
            entity.getAwardDate(),
            entity.getAgreementDate(),
            entity.getDetails(),
            entity.getObservations()
        );
    }
}
