package org.scouts105bentaya.core.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    private final HttpServletRequest request;

    public RequestService(HttpServletRequest request) {
        this.request = request;
    }

    public String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        String result;
        if (xfHeader != null) result = xfHeader.split(",")[0];
        else result = request.getRemoteAddr();
        if (result.contains(":") && result.contains(".")) result = result.split(":")[0];
        return result;
    }
}
