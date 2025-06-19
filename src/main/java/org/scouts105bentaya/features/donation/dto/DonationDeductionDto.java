package org.scouts105bentaya.features.donation.dto;

public record DonationDeductionDto(
    String deductionPercent,
    Character recurrency
) {
}