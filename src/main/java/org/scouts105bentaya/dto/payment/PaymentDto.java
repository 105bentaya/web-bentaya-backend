package org.scouts105bentaya.dto.payment;

import org.scouts105bentaya.enums.PaymentType;

import java.time.ZonedDateTime;

public record PaymentDto(
    Integer id,
    String orderNumber,
    Integer status,
    PaymentType paymentType,
    ZonedDateTime modificationDate,
    Integer amount
) {
}
