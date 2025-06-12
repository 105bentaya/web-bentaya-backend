package org.scouts105bentaya.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.payment.WebBentayaPaymentNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalControllerInternalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String WEB_BENTAYA_EXCEPTION = "webBentayaError";

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        @NonNull HttpRequestMethodNotSupportedException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
            log.warn(
                "Tried to request URI '{}' from address '{}'",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr()
            );
        } else {
            log.warn("Tried to request: {}", request);
        }
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }

    @ExceptionHandler(WebBentayaBadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(WebBentayaBadRequestException ex) {
        log.warn("Bad Request: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(getExceptionMessage(ex));
    }

    @ExceptionHandler(WebBentayaConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(WebBentayaConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(getExceptionMessage(ex));
    }

    @ExceptionHandler({WebBentayaNotFoundException.class, WebBentayaUserNotFoundException.class, WebBentayaRoleNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(WebBentayaNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(getExceptionMessage(ex, "El recurso solicitado no pudo ser encontrado"));
    }

    @ExceptionHandler(WebBentayaErrorException.class)
    public ResponseEntity<Map<String, String>> handleInternalException(WebBentayaErrorException ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(getExceptionMessage(ex));
    }

    @ExceptionHandler(WebBentayaForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(WebBentayaForbiddenException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(getExceptionMessage(ex));
    }

    @ExceptionHandler(WebBentayaUnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(WebBentayaUnauthorizedException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(getExceptionMessage(ex));
    }

    @ExceptionHandler(WebBentayaAuthServiceException.class)
    public ResponseEntity<Map<String, String>> handleAuthServiceException(WebBentayaAuthServiceException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_ACCEPTABLE)
            .body(getExceptionMessage(ex, "No se ha podido verificar la validez del usuario"));
    }

    @ExceptionHandler(WebBentayaPaymentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePaymentStatusException(WebBentayaAuthServiceException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(getExceptionMessage(ex, "El pago sobre el que se quiere realizar la operación no ha sido encontrado"));
    }

    @ExceptionHandler(WebBentayaScouterHasNoGroupException.class)
    public ResponseEntity<Map<String, String>> handleScouterHasNoGroupException(WebBentayaScouterHasNoGroupException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(getExceptionMessage(ex, "El scouter no tiene ninguna unidad asignada, por lo que no puede realizar esta operación"));
    }

    private Map<String, String> getExceptionMessage(RuntimeException ex) {
        return Map.of(WEB_BENTAYA_EXCEPTION, ex.getMessage());
    }

    private Map<String, String> getExceptionMessage(RuntimeException ex, String msg) {
        if (ex.getMessage() != null && !ex.getMessage().isBlank()) msg = ex.getMessage();
        return Map.of(WEB_BENTAYA_EXCEPTION, msg);
    }
}
