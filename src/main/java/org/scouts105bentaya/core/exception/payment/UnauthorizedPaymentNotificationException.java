package org.scouts105bentaya.core.exception.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedPaymentNotificationException extends RuntimeException {

    public UnauthorizedPaymentNotificationException(String message) {
        super(message);
    }
}
