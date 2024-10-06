package org.scouts105bentaya.dto.payment;

import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.entity.Payment;

@Getter
@Setter
public class PaymentFormDataRequestDto {
    private Payment payment;
    private String okUrl;
    private String koUrl;
}
