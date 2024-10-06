package org.scouts105bentaya.dto.payment;

import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.enums.PaymentType;

@Getter
@Setter
public class PaymentInfoDto {
    private Integer amount;
    private PaymentType paymentType;
}
