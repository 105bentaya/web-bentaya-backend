package org.scouts105bentaya.features.payment.dto;

import org.scouts105bentaya.features.payment.Payment;

public record PaymentFormDataRequestDto(
    Payment payment,
    String okUrl,
    String koUrl
) {
}
