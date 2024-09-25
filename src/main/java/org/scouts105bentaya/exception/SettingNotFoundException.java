package org.scouts105bentaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Setting not found")
public class SettingNotFoundException extends RuntimeException {

    public SettingNotFoundException() {
        super();
    }

}
