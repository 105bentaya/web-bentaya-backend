package org.scouts105bentaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.GONE, reason = "User has already changed its password")
public class UserHasAlreadyChangedPasswordException extends RuntimeException {
    public UserHasAlreadyChangedPasswordException(String message) {
        super(message);
    }
}
