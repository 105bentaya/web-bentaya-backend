package org.scouts105bentaya.core.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
public class UserHasReachedMaxLoginAttemptsException extends RuntimeException {
    public UserHasReachedMaxLoginAttemptsException(String message) {
        super(message);
    }
}
