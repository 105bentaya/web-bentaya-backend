package org.scouts105bentaya.security;

import jakarta.servlet.http.HttpServletRequest;
import org.scouts105bentaya.security.service.LoginAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final HttpServletRequest httpServletRequest;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(HttpServletRequest httpServletRequest, LoginAttemptService loginAttemptService) {
        this.httpServletRequest = httpServletRequest;
        this.loginAttemptService = loginAttemptService;
    }

    //TODO: ver cual es la diferencia entre esto y el requestService.getClientIP()
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(httpServletRequest.getRemoteAddr())) {
            loginAttemptService.loginFailed(httpServletRequest.getRemoteAddr());
        } else {
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }
    }
}
