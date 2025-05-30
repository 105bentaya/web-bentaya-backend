package org.scouts105bentaya.features.special_member.dto.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.special_member.DonationType;
import org.scouts105bentaya.shared.GenericConstants;

import java.time.LocalDate;

public record SpecialMemberDonationFormDto(
    @NotNull LocalDate date,
    @NotNull DonationType type,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String inKindDonationType,
    @Min(1) Integer amount,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String paymentType,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String bankAccount,
    @Length(max = 511) String notes
) {
}
