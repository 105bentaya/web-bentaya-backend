package org.scouts105bentaya.dto.payment;

public class PaymentFormDataDto {

    private String Ds_SignatureVersion;
    private String Ds_MerchantParameters;
    private String Ds_Signature;

    public String getDs_SignatureVersion() {
        return Ds_SignatureVersion;
    }

    public void setDs_SignatureVersion(String ds_SignatureVersion) {
        this.Ds_SignatureVersion = ds_SignatureVersion;
    }

    public String getDs_MerchantParameters() {
        return Ds_MerchantParameters;
    }

    public void setDs_MerchantParameters(String ds_MerchantParameters) {
        this.Ds_MerchantParameters = ds_MerchantParameters;
    }

    public String getDs_Signature() {
        return Ds_Signature;
    }

    public void setDs_Signature(String ds_Signature) {
        this.Ds_Signature = ds_Signature;
    }

}
