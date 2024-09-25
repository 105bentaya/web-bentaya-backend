package org.scouts105bentaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Confirmation not found")
public class ConfirmationNotFoundException extends RuntimeException {
    public ConfirmationNotFoundException() {
        super();
    }
}
