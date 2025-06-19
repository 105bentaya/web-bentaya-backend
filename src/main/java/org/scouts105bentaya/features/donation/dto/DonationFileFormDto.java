package org.scouts105bentaya.features.donation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record DonationFileFormDto(
    @NotNull @Min(2000) @Max(9999) Integer fiscalYear,
    @NotNull @Min(100000000) @Max(999999999) Integer declarantRepresentativePhone,
    @NotNull @Length(max = 40) String declarantRepresentativeName,
    @NotNull @Length(max = 40) String declarantRepresentativeSurname,
    @NotNull @Min(0) @Max(10000) Integer autonomousCommunityDeduction
) {
}
