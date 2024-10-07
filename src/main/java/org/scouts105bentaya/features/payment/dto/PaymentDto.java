package org.scouts105bentaya.features.payment.dto;

import org.scouts105bentaya.features.payment.PaymentTypeEnum;

import java.time.ZonedDateTime;

public record PaymentDto(
    Integer id,
    String orderNumber,
    Integer status,
    PaymentTypeEnum paymentType,
    ZonedDateTime modificationDate,
    Integer amount
) {
}
