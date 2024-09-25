package org.scouts105bentaya.dto.payment;

import org.scouts105bentaya.entity.Payment;

public class PaymentFormDataRequestDto {

    private Payment payment;
    private String okUrl;
    private String koUrl;

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getOkUrl() {
        return okUrl;
    }

    public void setOkUrl(String okUrl) {
        this.okUrl = okUrl;
    }

    public String getKoUrl() {
        return koUrl;
    }

    public void setKoUrl(String koUrl) {
        this.koUrl = koUrl;
    }
}
