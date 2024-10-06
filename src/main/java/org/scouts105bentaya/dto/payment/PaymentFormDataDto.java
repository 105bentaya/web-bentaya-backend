package org.scouts105bentaya.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentFormDataDto {
    private String Ds_SignatureVersion;
    private String Ds_MerchantParameters;
    private String Ds_Signature;
}
