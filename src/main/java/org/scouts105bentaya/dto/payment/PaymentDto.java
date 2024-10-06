package org.scouts105bentaya.dto.payment;

import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.enums.PaymentType;

import java.time.ZonedDateTime;

@Getter
@Setter
public class PaymentDto {
    private Integer id;
    private String orderNumber;
    private Integer status;
    private PaymentType paymentType;
    private ZonedDateTime modificationDate;
    private Integer amount;
}
