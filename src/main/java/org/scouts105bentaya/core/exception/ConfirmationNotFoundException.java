package org.scouts105bentaya.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Confirmation not found")
public class ConfirmationNotFoundException extends RuntimeException {
    public ConfirmationNotFoundException() {
        super();
    }
}
