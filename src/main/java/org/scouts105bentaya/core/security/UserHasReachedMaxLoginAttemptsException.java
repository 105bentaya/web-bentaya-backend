package org.scouts105bentaya.core.security;

import org.springframework.security.core.AuthenticationException;

public class UserHasReachedMaxLoginAttemptsException extends AuthenticationException {
    public UserHasReachedMaxLoginAttemptsException(String message) {
        super(message);
    }
}
