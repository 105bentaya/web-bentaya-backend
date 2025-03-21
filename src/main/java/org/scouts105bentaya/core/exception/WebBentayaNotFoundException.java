package org.scouts105bentaya.core.exception;

public class WebBentayaNotFoundException extends RuntimeException {
    public WebBentayaNotFoundException(String message) {
        super(message);
    }

    public WebBentayaNotFoundException() {
        super("Recurso no encontrado");
    }
}
