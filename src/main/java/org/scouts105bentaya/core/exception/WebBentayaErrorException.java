package org.scouts105bentaya.core.exception;

public class WebBentayaErrorException extends RuntimeException {
    public WebBentayaErrorException(String message) { //todo, when this error is throwed, somehow notify admins
        super(message);
    }
}
