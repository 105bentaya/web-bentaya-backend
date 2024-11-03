package org.scouts105bentaya.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class ErrorResponseHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ErrorResponseHandler() {
    }

    public static void authErrorHandler(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("webBentayaAuthError", message)));
    }
}
