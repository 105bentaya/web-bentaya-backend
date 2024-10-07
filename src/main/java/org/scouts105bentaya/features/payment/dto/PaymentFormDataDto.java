package org.scouts105bentaya.features.payment.dto;

public record PaymentFormDataDto(
    String Ds_SignatureVersion,
    String Ds_MerchantParameters,
    String Ds_Signature
) {
}
