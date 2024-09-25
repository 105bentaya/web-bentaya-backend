package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.payment.PaymentFormDataDto;
import org.scouts105bentaya.dto.payment.PaymentFormDataRequestDto;
import org.scouts105bentaya.dto.payment.PaymentInfoDto;
import org.scouts105bentaya.entity.Payment;
import org.scouts105bentaya.enums.PaymentType;

import java.util.List;

public interface PaymentService {

    Payment createPayment(PaymentInfoDto paymentInfoDto);

    List<Payment> findAll();

    Payment findById(Integer id);

    Payment savePayment(Payment payment);

    PaymentFormDataDto getPaymentFormData(PaymentFormDataRequestDto requestDto);

    void paymentConfirmation(PaymentFormDataDto response, PaymentType type);
}
