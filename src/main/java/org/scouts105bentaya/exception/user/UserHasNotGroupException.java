package org.scouts105bentaya.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserHasNotGroupException extends RuntimeException {

    public UserHasNotGroupException(String message) {
        super(message);
    }
}
