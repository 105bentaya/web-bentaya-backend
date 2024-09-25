package org.scouts105bentaya.dto.payment;

import org.scouts105bentaya.enums.PaymentType;

public class PaymentInfoDto {

    private Integer amount;
    private PaymentType paymentType;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}
