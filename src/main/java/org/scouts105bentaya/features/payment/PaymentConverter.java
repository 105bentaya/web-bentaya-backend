package org.scouts105bentaya.features.payment;

import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.features.payment.dto.PaymentDto;
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
