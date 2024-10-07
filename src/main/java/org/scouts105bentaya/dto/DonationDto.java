package org.scouts105bentaya.dto;

import org.scouts105bentaya.enums.DonationFrequency;
import org.scouts105bentaya.enums.SingleDonationPaymentType;

import java.time.ZonedDateTime;

public record DonationDto(
    Integer id,
    String name,
    String firstSurname,
    String secondSurname,
    String cif,
    String phone,
    String email,
    Boolean deduct,
    Integer amount,
    DonationFrequency frequency,
    SingleDonationPaymentType singleDonationPaymentType,
    String iban,
    Integer status,
    ZonedDateTime creationDate
) {
}
