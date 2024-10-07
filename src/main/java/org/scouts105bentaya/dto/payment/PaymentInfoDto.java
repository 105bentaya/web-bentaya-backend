package org.scouts105bentaya.dto.payment;

import org.scouts105bentaya.enums.PaymentType;

public record PaymentInfoDto(
    Integer amount,
    PaymentType paymentType
) {
}
