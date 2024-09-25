package org.scouts105bentaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BasicMessageException extends RuntimeException {
    public BasicMessageException(String message) {
        super(message);
    }
}
