package org.scouts105bentaya.core.exception.payment;

public class PaymentException extends RuntimeException {
    public PaymentException() {
        super();
    }

    public PaymentException(String message) {
        super(message);
    }
}