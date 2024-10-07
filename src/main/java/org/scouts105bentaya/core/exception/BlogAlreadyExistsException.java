package org.scouts105bentaya.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class BlogAlreadyExistsException extends RuntimeException {

    public BlogAlreadyExistsException(String message) {
        super(message);
    }
}
