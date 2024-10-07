package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.payment.PaymentDto;
import org.scouts105bentaya.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentConverter extends GenericConverter<Payment, PaymentDto> {

    @Override
    public Payment convertFromDto(PaymentDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public PaymentDto convertFromEntity(Payment payment) {
        return new PaymentDto(
            payment.getId(),
            payment.getOrderNumber(),
            payment.getStatus(),
            payment.getPaymentType(),
            payment.getModificationDate(),
            payment.getAmount()
        );
    }
}
