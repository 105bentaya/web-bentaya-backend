package org.scouts105bentaya.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Scout not found")
public class ScoutNotFoundException extends RuntimeException {
    public ScoutNotFoundException() {
        super();
    }
}
