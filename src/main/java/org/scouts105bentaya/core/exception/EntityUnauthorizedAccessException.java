package org.scouts105bentaya.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Unauthorized access to resource")
public class EntityUnauthorizedAccessException extends RuntimeException {
    public EntityUnauthorizedAccessException(String message) {
        super(message);
    }
}
