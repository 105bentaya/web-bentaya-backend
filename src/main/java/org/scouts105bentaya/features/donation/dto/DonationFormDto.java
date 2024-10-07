package org.scouts105bentaya.features.donation.dto;

import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.features.donation.enums.DonationFrequency;
import org.scouts105bentaya.features.donation.enums.SingleDonationPaymentType;

public record DonationFormDto(
    @NotNull String name,
    @NotNull String firstSurname,
    @NotNull String secondSurname,
    @NotNull String cif,
    @NotNull String phone,
    @NotNull String email,
    @NotNull Boolean deduct,
    @NotNull Integer amount,
    @NotNull DonationFrequency frequency,
    SingleDonationPaymentType singleDonationPaymentType,
    String iban
) {
}