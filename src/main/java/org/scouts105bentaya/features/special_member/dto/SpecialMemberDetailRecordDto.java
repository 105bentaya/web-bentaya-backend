package org.scouts105bentaya.features.special_member.dto;

import org.scouts105bentaya.features.special_member.entity.SpecialMember;
import org.scouts105bentaya.features.special_member.entity.SpecialMemberDonation;
import org.scouts105bentaya.features.special_member.enums.SpecialMemberRole;

import java.time.LocalDate;
import java.util.List;

public record SpecialMemberDetailRecordDto(
    int id,
    SpecialMemberRole role,
    int roleCensus,
    LocalDate awardDate,
    LocalDate agreementDate,
    String details,
    String observations,
    List<SpecialMemberDonation> donations
) {
    public static SpecialMemberDetailRecordDto fromEntity(SpecialMember entity) {
        return new SpecialMemberDetailRecordDto(
            entity.getId(),
            entity.getRole(),
            entity.getRoleCensus(),
            entity.getAwardDate(),
            entity.getAgreementDate(),
            entity.getDetails(),
            entity.getObservations(),
            entity.getDonations()
        );
    }
}
