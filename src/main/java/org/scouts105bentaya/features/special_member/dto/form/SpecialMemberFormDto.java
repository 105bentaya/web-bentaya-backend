package org.scouts105bentaya.features.special_member.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.special_member.SpecialMemberRole;
import org.scouts105bentaya.shared.GenericConstants;

import java.time.LocalDate;

public record SpecialMemberFormDto(
    @NotNull SpecialMemberRole role,
    @NotNull int roleCensus,
    LocalDate agreementDate,
    LocalDate awardDate,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String details,
    @Length(max = GenericConstants.MYSQL_TEXT_LENGTH) String observations,
    Integer scoutId,
    Integer personId,
    @Valid SpecialMemberPersonFormDto person
) {
}
