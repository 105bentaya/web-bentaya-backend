package org.scouts105bentaya.core.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserHasNotScoutsException extends RuntimeException {

    public UserHasNotScoutsException(String message) {
        super(message);
    }
}
