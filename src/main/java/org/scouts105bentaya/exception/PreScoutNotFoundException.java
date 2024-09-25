package org.scouts105bentaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Pre scout not found")
public class PreScoutNotFoundException extends RuntimeException {
    public PreScoutNotFoundException() {
        super();
    }

}
