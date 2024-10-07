package org.scouts105bentaya.dto.payment;

import org.scouts105bentaya.entity.Payment;

public record PaymentFormDataRequestDto(
    Payment payment,
    String okUrl,
    String koUrl
) {
}
