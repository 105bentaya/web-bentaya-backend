package org.scouts105bentaya.core.exception.user;

public class UserHasReachedMaxLoginAttemptsException extends RuntimeException {
    public UserHasReachedMaxLoginAttemptsException(String message) {
        super(message);
    }
}
