package org.scouts105bentaya.features.payment.dto;

import org.scouts105bentaya.features.payment.PaymentTypeEnum;

public record PaymentInfoDto(
    Integer amount,
    PaymentTypeEnum paymentType
) {
}
