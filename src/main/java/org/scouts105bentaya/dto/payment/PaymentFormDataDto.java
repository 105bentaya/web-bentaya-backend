package org.scouts105bentaya.dto.payment;

public record PaymentFormDataDto(
    String Ds_SignatureVersion,
    String Ds_MerchantParameters,
    String Ds_Signature
) {
}
